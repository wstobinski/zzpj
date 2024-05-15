import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Team} from "../../model/team.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {TeamsService} from "../../services/teams.service";
import {League} from "../../model/league.model";
import {LeagueService} from "../../services/league.service";

@Component({
  selector: 'app-league-management',
  templateUrl: './league-management.component.html',
  styleUrls: ['./league-management.component.scss'],
})
export class LeagueManagementComponent  implements OnInit {

  @Input() league: League;
  @Output() leagueEditedEmitter: EventEmitter<League> = new EventEmitter<League>();
  @Output() cancelLeagueEditedEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() hasUnsavedChangesEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  hasUnsavedChanges = false;
  leagueFormGroup: FormGroup;
  availableTeams: Team[];
  teamsSelected: Team[];
  mode: 'EDIT' | 'ADD';
  teamsDetailsToggle: boolean = false;

  constructor(private formBuilder: FormBuilder,
              private utils: Utils,
              private teamsService: TeamsService,
              private leagueService: LeagueService) {
  }

  ngOnInit() {
    this.mode = this.league.uuid ? 'EDIT' : 'ADD';
    if (this.mode === 'EDIT') {
      this.teamsSelected = this.league.teams;
      this.leagueFormGroup = this.formBuilder.group({
        name: [this.league.name, [Validators.required]],
        teams: [this.league.teams, [Validators.required, this.utils.rangeValidator(3,12)]],
        startDate: [this.league.startDate, [Validators.required]]
      });
    } else {
      this.leagueFormGroup = this.formBuilder.group({
        name: ['', [Validators.required]],
        teams: [null, [Validators.required, this.utils.rangeValidator(3,12)]],
        startDate: [null, [Validators.required]]
      });
    }
    // todo getFreeTeams(). concat freeTeams with current league teams
    this.teamsService.getFreeAgents().then(r => {
      if (r.ok) {
        this.availableTeams = r.response;
      } else {
        this.utils.presentAlertToast('Wystąpił błąd przy pobieraniu danych formularza');
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast('Wystąpił błąd przy pobieraniu danych formularza. Twoja sesja wygasła zaloguj się ponownie');
      } else {
        this.utils.presentAlertToast('Wystąpił błąd przy pobieraniu danych formularza');
      }
    });


  }

  formHasError(fieldName: string, errorType: string) {
    return this.utils.formHasError(this.leagueFormGroup, fieldName, errorType);
  }

  markUnsavedChanges() {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = true;
      this.hasUnsavedChangesEmitter.emit(this.hasUnsavedChanges);
    }
  }
  onCancel() {
    console.log(this.leagueFormGroup)
    this.cancelLeagueEditedEmitter.emit(true);
  }
  onLeagueEdit() {

    const startDateString = this.leagueFormGroup.get('startDate').value as string;
    const midnightDate = startDateString.split('T')[0].concat("T00:00:00");
    this.leagueFormGroup.get('startDate').setValue(midnightDate);
    console.log(this.leagueFormGroup);
    this.league = Object.assign(this.league, this.leagueFormGroup.value);
    if (this.mode === 'EDIT') {
      this.leagueService.updateLeague(this.league).then(r => {
        if (r.ok) {
          this.utils.presentInfoToast("Edycja ligi zakończona sukcesem");
          this.leagueEditedEmitter.emit(this.league);
        } else {
          this.utils.presentAlertToast("Wystąpił błąd podczas edycji ligi");
        }
      }).catch(e => {
        console.log(e);
        if (e.status === 401) {
          this.utils.presentAlertToast("Wystąpił błąd podczas edycji ligi. Twoja sesja wygasła. Zaloguj się ponownie");
        } else {
          this.utils.presentAlertToast("Wystąpił błąd podczas edycji ligi");
        }
      });
    } else {
      this.leagueService.createLeague(this.league).then(r => {
        if (r.ok) {
          this.utils.presentInfoToast("Tworzenie ligi zakończono sukcesem");
          this.leagueEditedEmitter.emit(this.league);
        } else {
          this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia ligi");
        }
      }).catch(e => {
        console.log(e);
        if (e.status === 401) {
          this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia ligi. Twoja sesja wygasła. Zaloguj się ponownie");
        } else {
          this.utils.presentAlertToast("Wystąpił błąd podczas tworzenia ligi");
        }
      });
    }

  }

  protected readonly Team = Team;

  onToggleChange($event: any) {
    this.teamsDetailsToggle = $event.detail.checked;
  }
}
