import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";

@Component({
  selector: 'app-generate-teams-modal',
  templateUrl: './generate-teams-modal.component.html',
  styleUrls: ['./generate-teams-modal.component.scss'],
})
export class GenerateTeamsModalComponent  implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) {
  }

  @Input() title: string;
  hasUnsavedChanges: boolean = false;
  generateFormGroup: FormGroup;
  leagues: { name: string, id: number }[];
  seasons: number[];

  ngOnInit() {

    this.seasons = [2021, 2022, 2023];


    this.leagues = [
      {name: "Superliga (Polska)", id: 78},
      {name: "Opcja 2 (KRAJ)", id: 100},
      {name: "Opcja 3 (KRAJ)", id: 100},
    ]

    this.generateFormGroup = this.formBuilder.group({
      leagueId: [null, [Validators.required]],
      season: [null, [Validators.required]]

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

