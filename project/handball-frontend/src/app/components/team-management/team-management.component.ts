import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Team} from "../../model/team.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Player} from "../../model/player.model";
import {Utils} from "../../utils/utils";
import {PlayersService} from "../../services/players.service";
import {SelectChangeEventDetail, SelectCustomEvent} from "@ionic/angular";
import {TeamsService} from "../../services/teams.service";

@Component({
  selector: 'app-team-management',
  templateUrl: './team-management.component.html',
  styleUrls: ['./team-management.component.scss'],
})
export class TeamManagementComponent  implements OnInit {

  @Input() team: Team;
  @Output() teamEditedEmitter: EventEmitter<Team> = new EventEmitter<Team>();
  @Output() cancelTeamEditedEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() hasUnsavedChangesEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
  hasUnsavedChanges = false;
  teamCaptain: Player;
  teamFormGroup: FormGroup;
  availablePlayers: Player[];
  playersSelected: Player[];
  mode: 'EDIT' | 'ADD';

  constructor(private formBuilder: FormBuilder,
              private utils: Utils,
              private playersService: PlayersService,
              private teamsService: TeamsService) { }

  ngOnInit() {
    this.mode = this.team.uuid ? 'EDIT' : 'ADD';
    if (this.mode === 'EDIT') {
      this.playersSelected = this.team.players;
      this.teamCaptain = this.team.players.find((player) => player.captain);
      this.teamFormGroup = this.formBuilder.group({
        teamName: [this.team.teamName, [Validators.required]],
        players: [this.team.players, [Validators.required]],
        captain: [this.teamCaptain, [Validators.required]]
      });
    } else {
      this.teamFormGroup = this.formBuilder.group({
        teamName: ['', [Validators.required]],
        players: [null, [Validators.required]],
        captain: [null, [Validators.required]]
      });
    }
    // todo getFreeAgents(). concat freeAgents with current team players
    this.playersService.getAllPlayers().then(r => {
      if (r.ok) {
        this.availablePlayers = r.response;
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
    return this.utils.formHasError(this.teamFormGroup, fieldName, errorType);
  }

  markUnsavedChanges() {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = true;
      this.hasUnsavedChangesEmitter.emit(this.hasUnsavedChanges);
    }
  }

  onTeamEdit() {

    this.team = Object.assign(this.team, this.teamFormGroup.value);
    if (this.mode === 'EDIT') {
      const oldCaptain = this.teamCaptain;
      const newCaptain: Player = this.teamFormGroup.controls['captain'].value;
      newCaptain.captain = true;
      this.teamsService.updateTeam(this.team).then(r => {
        if (r.ok) {
          if (oldCaptain && newCaptain.uuid !== oldCaptain.uuid) {
            oldCaptain.captain = false;
            this.teamsService.changeCaptains(newCaptain, oldCaptain).then(r => {
              if (r && r.ok) {
                this.utils.presentInfoToast("Edycja zespołu zakończona sukcesem");
                this.teamEditedEmitter.emit(this.team);
              } else {
                this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu");
              }
            }).catch(e => {
              if (e.status === 401) {
                this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu. Twoja sesja wygasła, zaloguj się ponownie");
              } else {
                this.utils.presentAlertToast("Wystąpił błąd podczas edycji zespołu");
              }
            });
          }
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
    } else {
      console.log(this.team)
      this.teamsService.createTeam(this.team).then(r => {
        if (r.ok) {
          this.utils.presentInfoToast("Tworzenie zespołu zakończone sukcesem");
          this.teamEditedEmitter.emit(this.team);
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

  }

  onCancel() {
    console.log(this.teamFormGroup)
    this.cancelTeamEditedEmitter.emit(true);
  }
  compareWith(o1, o2) {
    if (!o1 || !o2) {
      return o1 === o2;
    }

    if (Array.isArray(o2)) {
      return o2.some((o) => o.id === o1.id);
    }

    return o1.id === o2.id;
  }

  onSelectedPlayersChange($event: SelectCustomEvent) {

    const players = this.teamFormGroup.controls['players'].value;
    const captainControl = this.teamFormGroup.controls['captain']
    if (!players.includes(captainControl.value)) {
      captainControl.setValue(null);
      captainControl.markAsTouched();
      // captainControl.setErrors({required: true});
    }

  }
}
