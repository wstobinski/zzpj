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
  @Input() mode: 'EDIT' | 'ADD';
  hasUnsavedChanges: boolean = false;
  playerFormGroup: FormGroup;
  ngOnInit() {

    if (this.mode === 'EDIT') {
      this.playerFormGroup = this.formBuilder.group({
        firstName: [this.player.firstName, [Validators.required, Validators.minLength(2)]],
        lastName: [this.player.lastName, [Validators.required, Validators.minLength(2)]],
        phoneNumber: [this.player.phoneNumber, [Validators.minLength(9), Validators.maxLength(9)]],
        pitchNumber: [this.player.pitchNumber, [Validators.required, Validators.min(1)]],
        email: [this.player.email, [Validators.email]],
        suspended: [this.player.suspended]

      });
    } else {
      this.playerFormGroup = this.formBuilder.group({
        firstName: ['', [Validators.required, Validators.minLength(2)]],
        lastName: ['', [Validators.required, Validators.minLength(2)]],
        phoneNumber: ['', [Validators.minLength(9), Validators.maxLength(9)]],
        pitchNumber: ['', [Validators.required, Validators.min(1)]],
        email: ['', [Validators.email]],
        suspended: [false]

      });
    }



  }

  onClose() {
    if (this.hasUnsavedChanges) {
      const infoString = this.mode === 'EDIT' ? 'edycję' : 'dodawanie';
      this.utils.presentYesNoActionSheet(`Czy na pewno chcesz anulować ${infoString} zawodnika? Masz niezapisane zmiany`, 'Tak', 'Nie',
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

    if (this.mode === 'EDIT') {
      this.player = Object.assign(this.player, this.playerFormGroup.value);
    } else {
      this.player = Object.assign(new Player(), this.playerFormGroup.value);
    }
    this.modalController.dismiss(this.player, this.mode);
  }
}

