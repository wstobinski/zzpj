import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Player} from "../../model/player.model";
import {PlayersService} from "../../services/players.service";

@Component({
  selector: 'app-generate-players-modal',
  templateUrl: './generate-players-modal.component.html',
  styleUrls: ['./generate-players-modal.component.scss'],
})
export class GeneratePlayersModalComponent implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) {
  }

  @Input() title: string;
  hasUnsavedChanges: boolean = false;
  generateFormGroup: FormGroup;
  nationalities: { name: string, translate: string }[]

  ngOnInit() {


    this.nationalities = [
      {name: 'Polska', translate: 'polish'},
      {name: 'Słowacja', translate: 'slovakian'},
      {name: 'Czechy', translate: 'czech'},
      {name: 'Wielka Brytania', translate: 'british'},
      {name: 'Stany Zjednoczone', translate: 'american'}];

    this.generateFormGroup = this.formBuilder.group({
      nationality: ['', [Validators.required]],
      numberOfPlayers: ['', [Validators.required, this.utils.zeroToHundredValidator, Validators.min(1)]],

    });
  }


  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet(`Czy na pewno chcesz anulować generowanie zawodników? Masz niezapisane zmiany`, 'Tak', 'Nie',
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


  formHasError(fieldName: string, errorType: string
  ) {
    return this.utils.formHasError(this.generateFormGroup, fieldName, errorType)
  }

  onSubmit() {

    const rawValue = this.generateFormGroup.getRawValue();
    this.modalController.dismiss(rawValue);
  }
}
