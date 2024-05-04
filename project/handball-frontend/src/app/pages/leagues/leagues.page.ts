import {Component, OnInit} from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {PopoverController} from "@ionic/angular";
import {ActionButton} from "../../model/action-button.model";
import {League} from "../../model/league.model";
import {LeagueService} from "../../services/league.service";
import {Utils} from "../../utils/utils";

@Component({
  selector: 'app-leagues',
  templateUrl: './leagues.page.html',
  styleUrls: ['./leagues.page.scss'],
})
export class LeaguesPage extends GenericPage implements OnInit {
  actionButtons: ActionButton[];
  leagues: League[];
  leagueToEdit: League;

  constructor(loadingService: LoadingService,
              popoverController: PopoverController,
              private leaguesService: LeagueService,
              private utils: Utils) {
    super(loadingService, popoverController);
  }

  override async ngOnInit() {
    super.ngOnInit();
    this.leagues = (await this.leaguesService.getAllLeagues()).response
    this.actionButtons = [
      {
        buttonName: "Zarządzaj ligą",
        buttonAction: this.onLeagueToEditSelected.bind(this)
      },
      {
        buttonName: "Usuń ligę",
        buttonAction: this.deleteLeague.bind(this),
        actionColor: 'danger'
      },
    ]
  }


  onLeagueToEditSelected(league: League) {
    this.leagueToEdit = league;

  }
  deleteLeague(league: League) {
    this.utils.presentYesNoActionSheet(`Czy na pewno chcesz usunąć ligę ${league.name}? Ta akcja jest nieodwracalna`, 'Tak, usuwam ligę', 'Nie',
      () => {
        this.leaguesService.deleteLeague(league.uuid).then(r => {
          if (r.ok) {
            this.leagues = this.leagues.filter(l => l.uuid !== league.uuid);
            this.utils.presentInfoToast(`Liga ${league.name} usunięta pomyślnie`);
          } else {
            this.utils.presentAlertToast(`Wystąpił błąd przy usuwaniu ligi`);
          }
        }).catch(error => {
          if (error.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu ligi. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd przy usuwaniu ligi");
          }
        });
      }, () => {

      });
  }

  onAddNewLeague() {
    this.leagueToEdit = new League()
  }

  onEditCancelled($event: boolean) {
    this.leagueToEdit = null;
  }

  async onTeamEdited($event: League) {
    this.leagueToEdit = null;
    this.leagues = (await this.leaguesService.getAllLeagues()).response
  }

  onHasUnsavedChanges($event: boolean) {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = $event;
    }
  }
}
