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

@Component({
  selector: 'app-teams',
  templateUrl: './teams.page.html',
  styleUrls: ['./teams.page.scss'],
})
export class TeamsPage extends GenericPage implements OnInit {

  constructor(private teamsService: TeamsService,
              private modalController: ModalController,
              private popoverController: PopoverController,
              private utils: Utils,
              loadingService: LoadingService) {
    super(loadingService);
  }

  teams: Team[];
  private actionButtons: ActionButton[];

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

  async openPopover(ev: any, team: Team) {
    const popover = await this.popoverController.create({
      component: ActionMenuPopoverComponent,
      componentProps: {
        actionButtons: this.actionButtons,
        actionObject: team
      },
      event: ev,
      translucent: true,
    });
    return await popover.present();
  }


  async openTeamDetailsModal(team: Team) {
    console.log("Entering teamDetails", team)
    const modal = await this.modalController.create({
      component: EditTeamModalComponent,
      componentProps: {
        team,
        title: `Edytuj zespół ${team.teamName}`
      }
    });
    modal.onWillDismiss().then(data => {
      console.log('Did dismiss', data);
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
