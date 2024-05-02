import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Player} from "../../model/player.model";

@Component({
  selector: 'app-edit-player-modal',
  templateUrl: './edit-player-modal.component.html',
  styleUrls: ['./edit-player-modal.component.scss'],
})
export class EditPlayerModalComponent implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) { }
  @Input() title: string;
  @Input() player: Player;
  hasUnsavedChanges: boolean = false;
  playerFormGroup: FormGroup;
  ngOnInit() {


    this.playerFormGroup = this.formBuilder.group({
      firstName: [this.player.firstName, [Validators.required, Validators.minLength(2)]],
      lastName: [this.player.lastName, [Validators.required, Validators.minLength(2)]],
      phoneNumber: [this.player.phoneNumber, [Validators.minLength(9), Validators.maxLength(9)]],
      pitchNumber: [this.player.pitchNumber, [Validators.required, Validators.min(1)]],
      email: [this.player.email, [Validators.email]],
      suspended: [this.player.suspended],
      captain: [this.player.captain]

    });

  }

  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet('Czy na pewno chcesz anulować edycję zawodnika? Masz niezapisane zmiany', 'Tak', 'Nie',
        () => {
          this.modalController.dismiss(null);
        }, () => {

        });
    } else {
      this.modalController.dismiss(null)
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


  formHasError(fieldName: string, errorType: string) {
    return this.utils.formHasError(this.playerFormGroup, fieldName, errorType)
  }

  onPlayerEdit() {
    this.player = Object.assign(this.player, this.playerFormGroup.value);
    this.modalController.dismiss(this.player, 'submit')
  }
}

