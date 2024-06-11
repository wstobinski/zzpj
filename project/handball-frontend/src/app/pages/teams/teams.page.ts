import {Component, OnDestroy, OnInit} from '@angular/core';
import {TeamsService} from "../../services/teams.service";
import {Team} from "../../model/team.model";
import {ModalController, PopoverController} from "@ionic/angular";
import {EditTeamModalComponent} from "../../components/edit-team-modal/edit-team-modal.component";
import {ActionButton} from "../../model/action-button.model";
import {Utils} from "../../utils/utils";
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {AuthCheckService} from "../../services/auth-check.service";
import {User} from "../../model/user.model";
import {UserService} from "../../services/user.service";
import {Subscription} from "rxjs";
import {GeneratePlayersModalComponent} from "../../components/generate-players-modal/generate-players-modal.component";
import {GenerateTeamsModalComponent} from "../../components/generate-teams-modal/generate-teams-modal.component";

@Component({
  selector: 'app-teams',
  templateUrl: './teams.page.html',
  styleUrls: ['./teams.page.scss'],
})
export class TeamsPage extends GenericPage implements OnInit, OnDestroy {

  constructor(private teamsService: TeamsService,
              private modalController: ModalController,
              private utils: Utils,
              private authCheckService: AuthCheckService,
              private userService: UserService,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  teams: Team[];
  teamToEdit: Team;
  actionButtons: ActionButton[];
  user: User;
  userSub: Subscription;

  override async ngOnInit() {
    super.ngOnInit();
    this.userSub = this.userService.getUser().subscribe(u => {
      this.user = u;
    });
    const teamsResponse = await this.teamsService.getAllTeams();
    let teamsToDisplay = [] as Team[];
    if (this.user.role === 'captain') {
      const allTeams: Team[] = teamsResponse.response;
      for (const team of allTeams) {

        const authResponse = await this.authCheckService.isCaptainOfTeam(team.uuid);
        if (authResponse.ok) {
          teamsToDisplay.push(team);
          break;
        }
      }
    } else {
      teamsToDisplay = teamsResponse.response;
    }
    this.teams = teamsToDisplay;
    this.actionButtons = [
      {
        buttonName: "Zarządzaj zespołem",
        buttonAction: this.onTeamToEditSelected.bind(this)
      },
      {
        buttonName: "Usuń zespół",
        buttonAction: this.deleteTeam.bind(this),
        actionColor: 'danger',
        displayCondition: this.isAdmin.bind(this)
      },
    ]

  }

  override ngOnDestroy() {
    super.ngOnDestroy();
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }


  deleteTeam(team: Team) {

    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć zespół ${team.teamName}? Ta akcja jest nieodwracalna`, 'Tak, usuwam zespół', 'Nie',
      () => {
      this.teamsService.deleteTeam(team.uuid).then(r => {
        if (r.ok) {
          this.teams = this.teams.filter(t => t.uuid !== team.uuid);
          this.utils.presentInfoToast(`Zespół ${team.teamName} usunięto pomyślnie`);
        } else {
          this.utils.presentAlertToast(`Wystąpił błąd przy usuwaniu zespołu`);
        }
      }).catch(error => {
        if (error.status === 401) {
          this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu zespołu. Twoja sesja wygasła. Zaloguj się ponownie");
        } else {
          this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu zespołu");
        }
      });
      }, () => {

      });

  }
  onTeamToEditSelected(team: Team) {
    this.teamToEdit = team;
  }

  onEditCancelled($event: boolean) {
    this.teamToEdit = null;
  }

  async onTeamEdited($event: {mode: 'EDIT' | 'ADD', team: Team}) {
    this.teamToEdit = null;
    if ($event.mode === 'ADD') {
      this.teams.push($event.team);
    }
  }

  onHasUnsavedChanges($event: boolean) {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = $event;
    }
  }

  onAddNewTeam() {
    this.teamToEdit = new Team();
  }

  isAdmin() {
    return this.user.role === 'admin';
  }

  async openGenerateTeamsModal() {
    const modal = await this.modalController.create({
      component: GenerateTeamsModalComponent,
      componentProps: {
        title: "Automatyczne generowania zespołów",
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data) {
        this.teamsService.generateTeams(data.data).then(async r => {
          if (r.ok) {
            this.teams = (await this.teamsService.getAllTeams()).response
            this.utils.presentInfoToast("Zespoły wygenerowane pomyślnie!");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas generowania zespołów");
          }
        }).catch(e => {
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas generowania zespołów. Twoja sesja wygasła, zaloguj się ponownie")
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas generowania zespołów")

          }
        })
      }
    });
    return await modal.present();
  }
}
