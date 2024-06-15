import { Component, OnInit } from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {Utils} from "../../utils/utils";
import {ModalController, PopoverController} from "@ionic/angular";
import {LoadingService} from "../../services/loading.service";
import {ActionButton} from "../../model/action-button.model";
import {RefereeService} from "../../services/referee.service";
import {Referee} from "../../model/referee.model";
import {EditRefereeModalComponent} from "../../components/edit-referee-modal/edit-referee-modal.component";
import {User} from "../../model/user.model";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-referees',
  templateUrl: './referees.page.html',
  styleUrls: ['./referees.page.scss'],
})
export class RefereesPage extends GenericPage implements OnInit {

  constructor(private refereeService: RefereeService,
              private utils: Utils,
              private modalController: ModalController,
              private userService: UserService,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  referees: Referee[];
  actionButtons: ActionButton[];

  override async ngOnInit() {
    super.ngOnInit();
    const refereesResponse = await this.refereeService.getAllReferees()

    this.referees = refereesResponse.response;
    console.log(this.referees)
    this.actionButtons = [
      {
        buttonName: "Edytuj sędziego",
        buttonAction: this.openRefereeDetailsModal.bind(this)
      },
      {
        buttonName: "Wygeneruj konto",
        buttonAction: this.generateRefereeAccount.bind(this),
      },
      {
        buttonName: "Usuń sędziego",
        buttonAction: this.deleteReferee.bind(this),
        actionColor: 'danger'
      },
    ]

  }


  async openRefereeDetailsModal(referee: Referee, mode: 'EDIT' | 'ADD' = 'EDIT') {
    const modal = await this.modalController.create({
      component: EditRefereeModalComponent,
      componentProps: {
        referee,
        title: mode == "EDIT" ? "Edytuj sędziego" : "Dodaj nowego sędziego",
        mode
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'EDIT') {
        this.refereeService.updateReferee(data.data as Referee).then(r => {
          if (r.ok) {
            this.utils.presentInfoToast("Edycja sędziego zakończona sukcesem");
            const responseReferee = r.response as Referee;

            const index = this.referees.findIndex(ref => ref.uuid === referee.uuid);

            if (index !== -1) {
              this.referees[index] = responseReferee;
            }
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji sędziego");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji sędziego. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji sędziego");
          }
        });
      } else if (data && data.data && data.role === 'ADD') {
        this.refereeService.addReferee(data.data).then(async r => {
          if (r.ok) {
            const newReferees = await this.refereeService.getAllReferees()
            this.referees = newReferees.response;
            this.utils.presentInfoToast("Utworzenie sędziego zakończone sukcesem");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia sędziego");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia sędziego. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia sędziego");
          }
        });
      }
    });
    return await modal.present();
  }

  deleteReferee(referee: Referee) {

    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć sędziego ${referee.firstName} ${referee.lastName}? Ta akcja jest nieodwracalna`, 'Tak, usuwam sędziego', 'Nie',
      () => {
        this.refereeService.deleteReferee(referee.uuid).then(async r => {
          if (r.ok) {
            this.referees = (await this.refereeService.getAllReferees()).response;
            this.utils.presentInfoToast(`Sędzia został usunięty pomyślnie`);
          } else {
            this.utils.presentAlertToast(`Wystąpił błąd przy usuwaniu sędziego`);
          }
        }).catch(error => {
          this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu sędziego");
        });
      }, () => {

      });

  }
  generateRefereeAccount(referee:Referee) {

    const newUser = new User();
    newUser.role = "arbiter";
    newUser.email = referee.email;
    if (!referee.email) {
      this.utils.presentAlertToast("Konto można wygenerować, tylko dla sędziego, który posiada adres email");
      return;
    }
    this.userService.generateAccount(newUser).then(r => {
      if (r.ok) {
        this.utils.presentInfoToast("Konto zostało poprawnie wygenerowane. Aktywacyjna wiadomość email została wysłana", 8000);
      } else {
        this.utils.presentAlertToast("Wystąpił błąd przy generowaniu konta")
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast("Wystąpił błąd przy generowaniu konta. Twoja sesja wygasła, zaloguj się ponownie");

      } else {
        this.utils.presentAlertToast("Wystąpił błąd przy generowaniu konta")

      }
    })

  }
}
