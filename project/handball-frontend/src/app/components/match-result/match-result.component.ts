import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {League} from "../../model/league.model";
import {Match} from "../../model/match.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Score} from "../../model/score.model";
import {MatchScoreDto} from "../../model/DTO/match-score.dto";
import {MatchService} from "../../services/match.service";

@Component({
  selector: 'app-match-result',
  templateUrl: './match-result.component.html',
  styleUrls: ['./match-result.component.scss'],
})
export class MatchResultComponent  implements OnInit {

  @Input() match: Match;
  @Output() matchFinishedEmitter: EventEmitter<Match> = new EventEmitter<Match>();
  @Output() cancelMatchFinishEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() hasUnsavedChangesEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  ballPossessionRegex: RegExp = new RegExp('^([1-9][0-9]?|100)$');
  scoreFormGroup: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private utils: Utils,
              private matchService: MatchService) { }

  ngOnInit() {

    this.scoreFormGroup = this.formBuilder.group({
      homeScore: this.formBuilder.group({
        goals: ['', [Validators.required, this.utils.oneToHundredValidator]],
        fouls: ['', this.utils.oneToHundredValidator],
        ballPossession: ['', this.utils.oneToHundredValidator],
        yellowCards: ['', this.utils.oneToHundredValidator],
        redCards: ['', this.utils.oneToHundredValidator],
        timePenalties: ['', this.utils.oneToHundredValidator]
      }),
      awayScore: this.formBuilder.group({
        goals: ['',  [Validators.required, this.utils.oneToHundredValidator]],
        fouls: ['', this.utils.oneToHundredValidator],
        ballPossession: ['', this.utils.oneToHundredValidator],
        yellowCards: ['', this.utils.oneToHundredValidator],
        redCards: ['', this.utils.oneToHundredValidator],
        timePenalties: ['', this.utils.oneToHundredValidator]
      })
    });

  }
  get homeScoreFormGroup() {
    return this.scoreFormGroup.get('homeScore') as FormGroup;
  }

  get awayScoreFormGroup() {
    return this.scoreFormGroup.get('awayScore') as FormGroup;
  }

  async submitForm() {
    const rawValue = this.scoreFormGroup.getRawValue();
    console.log(rawValue);
    const team1Score = Object.assign(new Score(), rawValue.homeScore);
    team1Score.teamId = this.match.homeTeam.uuid;
    const team2Score = Object.assign(new Score(), rawValue.awayScore);
    team2Score.teamId = this.match.awayTeam.uuid;
    team1Score.lostGoals = team2Score.goals;
    team2Score.lostGoals = team1Score.goals;
    const matchScoreDto: MatchScoreDto = {team1Score, team2Score};
    const matchCompletedResponse = await this.matchService.completeMatch(this.match, matchScoreDto);
    if (matchCompletedResponse.ok) {
      this.utils.presentInfoToast("Wyniki meczu zapisano pomyślnie!");
      this.match.finished = true;
      this.matchFinishedEmitter.emit(this.match);
    } else {
      this.utils.presentAlertToast("Wystąpił błąd podczas zapisywania wyników meczu");
      this.cancelMatchFinishEmitter.emit(true);
    }
  }

  onCancel() {
    this.cancelMatchFinishEmitter.emit(true);
  }

  markUnsavedChanges() {
    this.hasUnsavedChangesEmitter.emit(true);
    this.hasUnsavedChangesEmitter.complete();
  }

  onBallPossessionChanged(formName: 'homeScore' | 'awayScore') {
    const otherFormName = formName === "homeScore" ? "awayScore" : "homeScore";
    const ballPossession = this.scoreFormGroup.get(formName).get('ballPossession').value;
    if (this.ballPossessionRegex.test(ballPossession)) {
      const awayBallPossession = 100 - ballPossession as number;
      this.scoreFormGroup.get(otherFormName).get('ballPossession').setValue(awayBallPossession);
    }
  }

  formHasError(formName: string, fieldName: string, errorType: string) {
    return this.utils.formHasError(this.scoreFormGroup.get(formName) as FormGroup, fieldName, errorType)
  }
}
