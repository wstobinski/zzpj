import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Match} from "../../model/match.model";
import {Referee} from "../../model/referee.model";
import {RefereeService} from "../../services/referee.service";

@Component({
  selector: 'app-match-edit-modal',
  templateUrl: './match-edit-modal.component.html',
  styleUrls: ['./match-edit-modal.component.scss'],
})
export class MatchEditModalComponent  implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils,
              private refereeService: RefereeService) { }
  @Input() title: string;
  @Input() match: Match;
  hasUnsavedChanges: boolean = false;
  matchFormGroup: FormGroup;
  referees: Referee[];
  createPost: boolean = true;
  async ngOnInit() {
    this.referees = [this.match.referee];
    this.matchFormGroup = this.formBuilder.group({
      gameDate: [this.match.gameDate, [Validators.required]],
      referee: [this.match.referee, [Validators.required]]
    });
    const differentReferees: Referee[] = (await this.refereeService.getAllReferees()).response.filter(r => r.uuid !== this.match.referee.uuid);
    if (differentReferees) {
      this.referees.push(...differentReferees);
    }
  }

  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet('Czy na pewno chcesz anulować edycję meczu? Masz niezapisane zmiany', 'Tak', 'Nie',
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

  onMatchEdit() {
    this.match = Object.assign(this.match, this.matchFormGroup.value);
    this.modalController.dismiss({match: this.match, createPost: this.createPost}, 'submit');

  }

  formHasError(fieldName: string, errorType: string) {
    return this.utils.formHasError(this.matchFormGroup, fieldName, errorType)
  }
}
