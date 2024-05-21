import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Referee} from "../../model/referee.model";

@Component({
  selector: 'app-edit-referee-modal',
  templateUrl: './edit-referee-modal.component.html',
  styleUrls: ['./edit-referee-modal.component.scss'],
})
export class EditRefereeModalComponent implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) { }
  @Input() title: string;
  @Input() referee: Referee;
  @Input() mode: 'EDIT' | 'ADD';
  hasUnsavedChanges: boolean = false;
  refereeFormGroup: FormGroup;
  ngOnInit() {

    this.referee = {...this.referee};
    if (this.mode === 'EDIT') {
      this.refereeFormGroup = this.formBuilder.group({
        firstName: [this.referee.firstName, [Validators.required, Validators.minLength(2)]],
        lastName: [this.referee.lastName, [Validators.required, Validators.minLength(2)]],
        email: [this.referee.email, [Validators.email, Validators.required]],
        phoneNumber: [this.referee.phoneNumber, [Validators.minLength(9), Validators.maxLength(9)]]


      });
    } else {
      this.refereeFormGroup = this.formBuilder.group({
        firstName: ['', [Validators.required, Validators.minLength(2)]],
        lastName: ['', [Validators.required, Validators.minLength(2)]],
        email: ['', [Validators.email, Validators.required]],
        phoneNumber: ['', [Validators.minLength(9), Validators.maxLength(9)]]

      });
    }



  }

  onClose() {
    if (this.hasUnsavedChanges) {
      const infoString = this.mode === 'EDIT' ? 'edycję' : 'dodawanie';
      this.utils.presentYesNoActionSheet(`Czy na pewno chcesz anulować ${infoString} sędziego? Masz niezapisane zmiany`, 'Tak', 'Nie',
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
    return this.utils.formHasError(this.refereeFormGroup, fieldName, errorType)
  }

  onRefereeEdit() {

    if (this.mode === 'EDIT') {
      this.referee = Object.assign(this.referee, this.refereeFormGroup.value);
    } else {
      this.referee = Object.assign(new Referee(), this.refereeFormGroup.value);

    }

    this.modalController.dismiss(this.referee, this.mode);
  }
}
