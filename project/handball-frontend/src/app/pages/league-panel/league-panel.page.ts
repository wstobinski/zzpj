import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {GenericPage} from "../generic/generic.page";
import {LoadingService} from "../../services/loading.service";
import {ModalController, PopoverController} from "@ionic/angular";
import {League} from "../../model/league.model";
import {LeagueService} from "../../services/league.service";
import {Match} from "../../model/match.model";
import {Utils} from "../../utils/utils";
import {AuthService} from "../../services/auth.service";
import {Round} from "../../model/round.model";
import {TeamContest} from "../../model/team-contest";
import {TeamContestService} from "../../services/team-contest.service";
import {EditPlayerModalComponent} from "../../components/edit-player-modal/edit-player-modal.component";
import {Player} from "../../model/player.model";

@Component({
  selector: 'app-league-panel',
  templateUrl: './league-panel.page.html',
  styleUrls: ['./league-panel.page.scss'],
})
export class LeaguePanelPage extends GenericPage implements OnInit {
  league: League;
  currentSegment: 'STANDINGS' | 'MATCHES' = 'MATCHES';
  rounds: Round[];
  teamContests: TeamContest[];
  matchToFinish: Match;

  constructor(private route: ActivatedRoute,
              private leagueService: LeagueService,
              private utils: Utils,
              private authService: AuthService,
              private teamContestsService: TeamContestService,
              private modalController: ModalController,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.isLoading = true;
    this.route.paramMap.subscribe(
      {
        next: async params => {
          if (history.state && history.state.league) {
            this.league = history.state.league;
            console.log(history.state);
            this.isLoading = false;
          } else {
            this.league = (await this.leagueService.getLeagueById(params.get('leagueId'))).response;
            this.isLoading = false;
          }
          try {
            const roundsResponse = await this.leagueService.getRounds(this.league.uuid);
            if (roundsResponse.ok) {
              this.rounds = roundsResponse.response;
            } else {
              this.utils.presentAlertToast("Wystąpił błąd podczas pobierania harmonogramu ligi");
            }
          } catch (e) {
            if (e.status === 401) {
              this.utils.presentAlertToast("Wystąpił błąd podczas pobierania harmonogramu ligi. Twoja sesja wygasła, zaloguj się ponownie");
              this.authService.logout("/login");
            } else {
              this.utils.presentAlertToast("Wystąpił błąd podczas pobierania harmonogramu ligi");

            }
          }
          try {
            const teamContestsResponse = await this.teamContestsService.getForLeague(this.league.uuid);
            if (teamContestsResponse.ok) {
              this.teamContests = teamContestsResponse.response;
              console.log(this.teamContests);
            } else {
              this.utils.presentAlertToast("Wystąpił błąd podczas pobierania tabeli ligowej");
            }
          } catch (e) {
            if (e.status === 401) {
              this.utils.presentAlertToast("Wystąpił błąd podczas pobierania tabeli ligowej. Twoja sesja wygasła, zaloguj się ponownie");
              this.authService.logout("/login");
            } else {
              this.utils.presentAlertToast("Wystąpił błąd podczas pobierania tabeli ligowej");

            }
          }

        },
        error: (e) => {
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas pobierania danych ligi. Twoja sesja wygasła, zaloguj się ponownie");
            this.authService.logout("/login");
          }
        }
      });
  }

  onSegmentChanged($event: any) {
    this.currentSegment = $event.detail.value;
  }

  async onMatchFinishEntered(match: Match) {
   this.matchToFinish = match;
  }

  onEditCancelled($event: boolean) {
    this.matchToFinish = null;
  }

  onMatchFinished($event: Match) {
    this.matchToFinish = null;
  }

  onHasUnsavedChanges($event: boolean) {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = $event;
    }
  }
}
