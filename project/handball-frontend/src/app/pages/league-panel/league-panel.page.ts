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
import {EditTeamModalComponent} from "../../components/edit-team-modal/edit-team-modal.component";
import {Team} from "../../model/team.model";
import {MatchEditModalComponent} from "../../components/match-edit-modal/match-edit-modal.component";
import {MatchService} from "../../services/match.service";
import {Score} from "../../model/score.model";
import {User} from "../../model/user.model";
import {Subscription} from "rxjs";
import {UserService} from "../../services/user.service";
import {Referee} from "../../model/referee.model";
import {RefereeCommentModalComponent} from "../../components/referee-comment-modal/referee-comment-modal.component";
import {CommentsService} from "../../services/comments.service";
import {CommentDto} from "../../model/DTO/comment.dto";
import {AuthCheckService} from "../../services/auth-check.service";

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
  user: User;
  userSub: Subscription

  constructor(private route: ActivatedRoute,
              private leagueService: LeagueService,
              private matchService: MatchService,
              private utils: Utils,
              private authService: AuthService,
              private teamContestsService: TeamContestService,
              private modalController: ModalController,
              private userService: UserService,
              private commentsService: CommentsService,
              private authCheckService: AuthCheckService,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.userSub = this.userService.getUser().subscribe(u => {
      this.user = u;
    });

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
              for (let round of this.rounds) {

                for (let match of round.matches) {

                  if (!match.finished && this.user.role === 'arbiter') {
                    match.canEdit = (await this.authCheckService.isRefereeInMatch(match.uuid)).ok;
                  }
                  if (match.finished) {
                    const matchScores: Score[] = (await this.matchService.getMatchScores(match)).response;
                    match.homeTeamScore = matchScores[0].goals;
                    match.awayTeamScore = matchScores[0].lostGoals;
                    if (this.user.role === 'captain') {
                      match.canComment = (await this.authCheckService.isCaptainInMatch(match.uuid)).ok;
                    }
                  }

                }

              }
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

  async onMatchFinished($event: Match) {
    this.matchToFinish = null;
    this.teamContests = (await (this.teamContestsService.getForLeague(this.league.uuid))).response
  }

  onHasUnsavedChanges($event: boolean) {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = $event;
    }
  }

  async onMatchEdit(match: Match) {

    const modal = await this.modalController.create({
      component: MatchEditModalComponent,
      componentProps: {
        match: match,
        title: `Edytuj mecz`
      }
    });
    const matchCopy = {...match};
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'submit') {
        console.log(data.data);
        this.matchService.updateMatch(data.data as Match).then(r => {
          if (r.ok) {
            this.utils.presentInfoToast("Edycja meczu zakończona sukcesem!");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji meczu");
            match = matchCopy;
          }
        }).catch(e => {
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji meczu. Twoja sesja wygasła, zaloguj się ponownie");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas edycji meczu");
          }
          match = matchCopy;
        });
      }
    });
    return await modal.present();


  }

  async onRefereeCommentEntered(match: Match) {
    const modal = await this.modalController.create({
      component: RefereeCommentModalComponent,
      componentProps: {
        match,
        title: `Oceń pracę sędziego`
      }
    });
    modal.onWillDismiss().then(async data => {
      if (data && data.data && data.role === 'submit') {
        console.log(data.data);
        const commentData: CommentDto = {
          content: data.data,
          refereeId: match.referee.uuid,
          matchId: match.uuid,
          authorId: this.user.modelId
        }
        this.commentsService.addComment(commentData).then(r => {
          if (r.ok) {
            this.utils.presentInfoToast("Komentarz został dodany");
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas wystawiania komentarza")
          }
        }).catch(e => {
          if (e.status === 401) {
            this.utils.presentAlertToast("Wystąpił błąd podczas wystawiania komentarza. Twoja sesja wygasła, zaloguj się ponownie")
          } else {
            this.utils.presentAlertToast("Wystąpił błąd podczas wystawiania komentarza")

          }
        })
      }
    });
    return await modal.present();
  }
}
