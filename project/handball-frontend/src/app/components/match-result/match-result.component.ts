import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {League} from "../../model/league.model";
import {Match} from "../../model/match.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

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

  scoreFormGroup: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {

    this.scoreFormGroup = this.formBuilder.group({
      homeScore: this.formBuilder.group({
        goals: ['', Validators.required],
        fouls: [''],
        ballPossession: [''],
        yellowCards: [''],
        redCards: [''],
        timePenalties: ['']
      }),
      awayScore: this.formBuilder.group({
        goals: ['', Validators.required],
        fouls: [''],
        ballPossession: [''],
        yellowCards: [''],
        redCards: [''],
        timePenalties: ['']
      })
    });

  }
  get homeScoreFormGroup() {
    return this.scoreFormGroup.get('homeScore') as FormGroup;
  }

  get awayScoreFormGroup() {
    return this.scoreFormGroup.get('awayScore') as FormGroup;
  }

  submitForm() {

  }

  onCancel() {
    this.cancelMatchFinishEmitter.emit(true);
  }

  markUnsavedChanges() {
    this.hasUnsavedChangesEmitter.emit(true);
    this.hasUnsavedChangesEmitter.complete();
  }
}
