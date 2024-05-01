import {Component, Input, input, OnInit} from '@angular/core';
import {ModalController, ToggleCustomEvent} from "@ionic/angular";
import {Team} from "../../model/team.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Player} from "../../model/player.model";

@Component({
  selector: 'app-edit-team-modal',
  templateUrl: './edit-team-modal.component.html',
  styleUrls: ['./edit-team-modal.component.scss'],
})
export class EditTeamModalComponent  implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) { }
  @Input() title: string;
  @Input() team: Team;
  teamCaptain: Player;
  hasUnsavedChanges: boolean = false;
  teamFormGroup: FormGroup;
  showPlayers: boolean = false;
  ngOnInit() {

    this.teamCaptain = this.team.players.find((player) => player.captain);
    this.teamFormGroup = this.formBuilder.group({
      teamName: [this.team.teamName, [Validators.required]],
      captain: [this.teamCaptain, [Validators.required]]
    });

  }

  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet('Czy na pewno chcesz anulować edycję zespołu? Masz niezapisane zmiany', 'Tak', 'Nie',
        () => {
        this.modalController.dismiss(null);
      }, () => {

        });
    }
  }

  markUnsavedChanges() {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = true;
    }
    this.modalController.getTop().then(modal => {
      if (modal) {
        modal.backdropDismiss = !this.hasUnsavedChanges;
      }
    });
  }

  onTeamEdit() {

  }

  onShowPlayersToggle($event: ToggleCustomEvent) {
    this.showPlayers = $event.detail.checked
  }
  formHasError(fieldName: string, errorType: string) {
    return this.utils.formHasError(this.teamFormGroup, fieldName, errorType)
  }
}
