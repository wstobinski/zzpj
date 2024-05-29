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
        actionColor: 'danger'
      },
    ]

  }

  override ngOnDestroy() {
    super.ngOnDestroy();
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }


  async openTeamDetailsModal(team: Team) {
    const modal = await this.modalController.create({
      component: EditTeamModalComponent,
      componentProps: {
        team,
        title: `Edytuj zespół ${team?.teamName}`,
        mode: team? 'EDIT' : 'ADD'
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'EDIT') {
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
      } else if (data && data.data && data.role === 'ADD') {
        this.teamsService.createTeam(data.data.team).then(async r => {
          if (r.ok) {
            const newTeams = await this.teamsService.getAllTeams();
            this.teams = newTeams.response;
            this.utils.presentInfoToast("Utworzenie zespołu zakończono sukcesem");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zespołu");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zespołu. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia zespołu");
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
}
