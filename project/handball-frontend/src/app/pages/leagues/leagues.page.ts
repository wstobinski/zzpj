import {Component, OnInit} from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {ModalController, PopoverController} from "@ionic/angular";
import {ActionButton} from "../../model/action-button.model";
import {League} from "../../model/league.model";
import {LeagueService} from "../../services/league.service";
import {Utils} from "../../utils/utils";
import {EditPlayerModalComponent} from "../../components/edit-player-modal/edit-player-modal.component";
import {
  GenerateScheduleModalComponent
} from "../../components/generate-schedule-modal/generate-schedule-modal.component";
import {Router} from "@angular/router";

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
              private utils: Utils,
              private modalController: ModalController,
              private router: Router) {
    super(loadingService, popoverController);
  }

  override async ngOnInit() {
    super.ngOnInit();
    this.leagues = (await this.leaguesService.getAllLeagues()).response;
    console.log(this.leagues);
    this.actionButtons = [
      {
        buttonName: "Przejdź do panelu ligi",
        buttonAction: this.onLeaguePanelOpened.bind(this)
      },
      {
        buttonName: "Zarządzaj ligą",
        buttonAction: this.onLeagueToEditSelected.bind(this)
      },
      {
        buttonName: "Wygeneruj harmonogram",
        buttonAction: this.onLeagueGenerateSchedule.bind(this),
        displayCondition: this.canGenerateSchedule.bind(this)
      },
      {
        buttonName: "Usuń ligę",
        buttonAction: this.deleteLeague.bind(this),
        actionColor: 'danger',
        displayCondition: this.canDeleteLeague.bind(this)
      },
    ]
  }

  async onLeagueGenerateSchedule(league: League) {
    const modal = await this.modalController.create({
      component: GenerateScheduleModalComponent,
      componentProps: {
        title: "Generuj harmonogram ligi",
        league
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'submit') {
        this.leaguesService.generateSchedule(league.uuid, data.data).then(async r => {
          if (r.ok) {
            league.scheduleGenerated = true;
            this.utils.presentInfoToast("Generowanie harmonogramu przebiegło pomyślnie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas generowania harmonogramu");
          }
        }).catch(e => {
          console.log(e);
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas generowania harmonogramu. Twoja sesja wygasła. Zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas generowania harmonogramu");
          }
        });
      }
    });
    return await modal.present();


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
    this.leagues = (await this.leaguesService.getAllLeagues()).response;

  }

  onHasUnsavedChanges($event: boolean) {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = $event;
    }
  }

  onLeaguePanelOpened(league: League) {
    this.router.navigate(['/league-panel', league.uuid], { state: { league } });
  }

  canGenerateSchedule(league: League) {
    return !league.scheduleGenerated
  }

  canDeleteLeague(league: League) {
    return this.canGenerateSchedule(league);
  }
}
