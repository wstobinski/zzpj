import {Component, OnInit} from '@angular/core';
import {TeamsService} from "../../services/teams.service";
import {Team} from "../../model/team.model";
import {ModalController, PopoverController} from "@ionic/angular";
import {EditTeamModalComponent} from "../../components/edit-team-modal/edit-team-modal.component";
import {ActionMenuPopoverComponent} from "../../components/action-menu-popover/action-menu-popover.component";
import {ActionButton} from "../../model/action-button.model";
import {Utils} from "../../utils/utils";
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {Player} from "../../model/player.model";
import {PlayersService} from "../../services/players.service";

@Component({
  selector: 'app-teams',
  templateUrl: './teams.page.html',
  styleUrls: ['./teams.page.scss'],
})
export class TeamsPage extends GenericPage implements OnInit {

  constructor(private teamsService: TeamsService,
              private modalController: ModalController,
              private utils: Utils,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  teams: Team[];
  actionButtons: ActionButton[];

  override async ngOnInit() {
    super.ngOnInit();
    const teamsResponse = await this.teamsService.getAllTeams()
    console.log(teamsResponse)
    this.teams = teamsResponse.response;
    this.actionButtons = [
      {
        buttonName: "Edytuj zespół",
        buttonAction: this.openTeamDetailsModal.bind(this)
      },
      {
        buttonName: "Usuń zespół",
        buttonAction: this.deleteTeam.bind(this),
        actionColor: 'danger'
      },
    ]

  }


  async openTeamDetailsModal(team: Team) {
    const modal = await this.modalController.create({
      component: EditTeamModalComponent,
      componentProps: {
        team,
        title: `Edytuj zespół ${team.teamName}`
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'submit') {
        this.teamsService.updateTeam(data.data.team as Team).then(r => {
          if (r.ok) {
            if (data.data.oldCaptain.uuid !== data.data.newCaptain.uuid) {
              this.teamsService.changeCaptains(data.data.newCaptain, data.data.oldCaptain).then(r => {
                if (r && r.ok) {
                  this.utils.presentInfoToast("Edycja zespołu zakończona sukcesem");
                } else {
                  this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu");
                }
              }).catch(e => {
                this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu");
              })
            }
            team = r.response;
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu");
          }
        });
      }
    });
    return await modal.present();
  }

  deleteTeam(team: Team) {

    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć zespół ${team.teamName}? Ta akcja jest nieodwracalna`, 'Tak, usuwam zespół', 'Nie',
      () => {
      this.teamsService.deleteTeam(team.uuid).then(r => {
        if (r.ok) {
          this.utils.presentInfoToast(`Zespół ${team.teamName} usunięto pomyślnie`);
        } else {
          this.utils.presentAlertToast(`Wystąpił błąd przy usuwaniu zespołu`);
        }
      }).catch(error => {
        this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu zespołu");
      });
      this.utils.presentInfoToast(`Zespół ${team.teamName} usunięto pomyślnie`);
      }, () => {

      });

  }
}
