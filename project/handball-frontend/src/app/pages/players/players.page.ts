import {Component, OnDestroy, OnInit} from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {PlayersService} from "../../services/players.service";
import {ActionButton} from "../../model/action-button.model";
import {Utils} from "../../utils/utils";
import {Player} from "../../model/player.model";
import {ModalController, PopoverController} from "@ionic/angular";
import {EditPlayerModalComponent} from "../../components/edit-player-modal/edit-player-modal.component";
import {User} from "../../model/user.model";
import {UserService} from "../../services/user.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-players',
  templateUrl: './players.page.html',
  styleUrls: ['./players.page.scss'],
})
export class PlayersPage extends GenericPage implements OnInit, OnDestroy {


  constructor(private playersService: PlayersService,
              private utils: Utils,
              private modalController: ModalController,
              private userService: UserService,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  players: Player[];
  actionButtons: ActionButton[];
  user: User;
  userSub: Subscription;

  override async ngOnInit() {
    super.ngOnInit();
    this.userSub = this.userService.getUser().subscribe(u => {
      console.log('got user', u)
      this.user = u;
    });
    console.log("Making request")
    const playersResponse = await this.playersService.getAllPlayers()
    console.log(playersResponse)
    this.players = playersResponse.response;
    console.log(this.players)
    this.actionButtons = [
      {
        buttonName: "Edytuj zawodnika",
        buttonAction: this.openPlayerDetailsModal.bind(this),
        displayCondition: this.isAdmin.bind(this)
      },
      {
        buttonName: "Wygeneruj konto",
        buttonAction: this.generatePlayerAccount.bind(this),
        displayCondition: this.generateAccountActive.bind(this)
      },
      {
        buttonName: "Usuń zawodnika",
        buttonAction: this.deletePlayer.bind(this),
        actionColor: 'danger',
        displayCondition: this.isAdmin.bind(this)
      },
      {
        buttonName: "Zawieś zawodnika",
        buttonAction: this.suspendPlayer.bind(this),
        actionColor: 'danger',
        displayCondition: this.suspendPlayerActive.bind(this)
      },
      {
        buttonName: "Anuluj zawieszenie zawodnika",
        buttonAction: this.cancelSuspendPlayer.bind(this),
        displayCondition: this.cancelSuspendPlayerActive.bind(this)
      },
    ]

  }

  override ngOnDestroy() {
    super.ngOnDestroy();
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }


  async suspendPlayer(player: Player) {

    player.suspended = true;
    this.playersService.updatePlayer(player).then(r => {
      if (r.ok) {
        this.utils.presentInfoToast("Zawodnik został zawieszony");
      } else {
        this.utils.presentAlertToast("Wystąpił błąd podczas zawieszania zawodnika");
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast("Wystąpił błąd podczas zawieszania zawodnika. Twoja sesja wygasła, zaloguj się ponownie");
      } else {
        this.utils.presentAlertToast("Wystąpił błąd podczas zawieszania zawodnika");
      }
    });
  }

  async cancelSuspendPlayer(player: Player) {

    player.suspended = false;
    this.playersService.updatePlayer(player).then(r => {
      if (r.ok) {
        this.utils.presentInfoToast("Anulowano zawieszenie zawodnika");
      } else {
        this.utils.presentAlertToast("Wystąpił błąd podczas anulowania zawieszenie zawodnika");
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast("Wystąpił błąd podczas anulowania zawieszenia zawodnika. Twoja sesja wygasła, zaloguj się ponownie");
      } else {
        this.utils.presentAlertToast("Wystąpił błąd podczas anulowania zawieszenia zawodnika");
      }
    });
  }

  async openPlayerDetailsModal(player: Player, mode: 'EDIT' | 'ADD' = 'EDIT') {
    console.log("Entering playerDetails", player)
    const modal = await this.modalController.create({
      component: EditPlayerModalComponent,
      componentProps: {
        player,
        title: mode == "EDIT" ? "Edytuj zawodnika" : "Dodaj nowego zawodnika",
        mode
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'EDIT') {
        this.playersService.updatePlayer(data.data as Player).then(r => {
          if (r.ok) {
            this.utils.presentInfoToast("Edycja zawodnika zakończona sukcesem");
            const responsePlayer = r.response as Player;

            const index = this.players.findIndex(player => player.uuid === responsePlayer.uuid);

            if (index !== -1) {
              this.players[index] = responsePlayer;
            }
          } else {
            console.log(r);
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zawodnika");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zawodnika. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zawodnika");
          }
        });
      } else if (data && data.data && data.role === 'ADD') {
        this.playersService.addPlayer(data.data).then(async r => {
          if (r.ok) {
            const newPlayers = await this.playersService.getAllPlayers();
            this.players = newPlayers.response;
            this.utils.presentInfoToast("Utworzenie zawodnika zakończone sukcesem");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zawodnika");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zawodnika. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zawodnika");
          }
        });
      }
    });
    return await modal.present();
  }

  deletePlayer(player: Player) {

    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć zawodnika ${player.firstName} ${player.lastName}? Ta akcja jest nieodwracalna`, 'Tak, usuwam zawodnika', 'Nie',
      () => {
        this.playersService.deletePlayer(player.uuid).then(async r => {
          if (r.ok) {
            this.players = (await this.playersService.getAllPlayers()).response;
            this.utils.presentInfoToast(`Zawodnik został usunięty pomyślnie`);
          } else {
            this.utils.presentAlertToast(`Wystąpił błąd przy usuwaniu zawodnika`);
          }
        }).catch(error => {
          this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu zawodnika");
        });
      }, () => {

      });

  }

  generatePlayerAccount(player:Player) {

    const newUser = new User();
    newUser.role = "captain";
    newUser.email = player.email;
    if (!player.captain) {
      this.utils.presentAlertToast("Konto można wygenerować, tylko dla zawodnika, który jest kapitanem");
      return;
    }
    if (!player.email) {
      this.utils.presentAlertToast("Konto można wygenerować, tylko dla zawodnika, który posiada adres email");
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

  private generateAccountActive(player: Player) {
    return this.isAdmin() && player.captain && player.email;
  }

  protected isAdmin() {
    return this.user.role === 'admin';
  }

  private suspendPlayerActive(player: Player) {
    return this.isReferee() && !player.suspended;
  }

  private cancelSuspendPlayerActive(player: Player) {
    return this.isReferee() && player.suspended;
  }

  private isReferee() {
    return this.user.role === 'arbiter';
  }
}
