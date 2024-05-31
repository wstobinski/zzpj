import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {RefereeService} from "../../services/referee.service";
import {Match} from "../../model/match.model";
import {Referee} from "../../model/referee.model";

@Component({
  selector: 'app-referee-comment-modal',
  templateUrl: './referee-comment-modal.component.html',
  styleUrls: ['./referee-comment-modal.component.scss'],
})
export class RefereeCommentModalComponent  implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils,
              private refereeService: RefereeService) { }
  @Input() title: string;
  @Input() match: Match;
  @Input() referee: Referee;
  hasUnsavedChanges: boolean = false;
  commentControl: FormControl;
  async ngOnInit() {

    this.commentControl = new FormControl<string>('', [Validators.required])
  }

  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet('Czy na pewno chcesz anulować wystawianie oceny sędziego? Masz niezapisane zmiany', 'Tak', 'Nie',
        () => {
          this.modalController.dismiss(null);
        }, () => {

        });
    } else {
      this.modalController.dismiss(null);
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

  onCommentAdded() {
    this.modalController.dismiss(this.commentControl.value, 'submit');

  }
}

