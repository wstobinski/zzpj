import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Referee} from "../../model/referee.model";
import {Post} from "../../model/post.model";

@Component({
  selector: 'app-post-modal',
  templateUrl: './post-modal.component.html',
  styleUrls: ['./post-modal.component.scss'],
})
export class PostModalComponent implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) {
  }

  @Input() title: string;
  @Input() post: Post;
  @Input() mode: 'EDIT' | 'ADD';
  hasUnsavedChanges: boolean = false;
  postFormGroup: FormGroup;

  ngOnInit() {

    if (this.mode === 'EDIT') {
      this.postFormGroup = this.formBuilder.group({
        title: [this.post.title, [Validators.required, Validators.minLength(2)]],
        content: [this.post.content, [Validators.required, Validators.minLength(2)]]
      });
    } else {
      this.postFormGroup = this.formBuilder.group({
        title: ["", [Validators.required, Validators.minLength(2)]],
        content: ["", [Validators.required, Validators.minLength(2)]]
      });
    }
  }

  onClose() {
    if (this.hasUnsavedChanges) {
      const infoString = this.mode === 'EDIT' ? 'edycję' : 'dodawanie';
      this.utils.presentYesNoActionSheet(`Czy na pewno chcesz anulować ${infoString} ogłoszenia? Masz niezapisane zmiany`, 'Tak', 'Nie',
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
    return this.utils.formHasError(this.postFormGroup, fieldName, errorType)
  }

  onRefereeEdit() {

    if (this.mode === 'EDIT') {
      this.post = Object.assign(this.post, this.postFormGroup.value);
    } else {
      this.post = Object.assign(new Post(), this.postFormGroup.value);

    }

    this.modalController.dismiss(this.post, this.mode);
  }
}
