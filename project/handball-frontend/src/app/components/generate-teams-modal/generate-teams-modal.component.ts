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
  leagues: { name: string, id: number, nationality: string }[];
  seasons: number[];

  ngOnInit() {

    this.seasons = [2021, 2022, 2023];


    this.leagues = [
      {name: "Superliga (Polska)", id: 78, nationality: 'polish'},
      {name: "Extraliga (Czechy)", id: 12, nationality: 'czech'},
      {name: "1. Division (Dania)", id: 15, nationality: 'danish'},
    ]

    this.generateFormGroup = this.formBuilder.group({
      league: [null, [Validators.required]],
      season: [null, [Validators.required]],
      generatePlayers: [true, [Validators.required]],

    });
  }


  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet(`Czy na pewno chcesz anulować generowanie zespołów? Masz niezapisane zmiany`, 'Tak', 'Nie',
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
    rawValue.nationality = rawValue.league.nationality;
    rawValue.leagueId = rawValue.league.id;
    delete rawValue.league;
    this.modalController.dismiss(rawValue);
  }
}

