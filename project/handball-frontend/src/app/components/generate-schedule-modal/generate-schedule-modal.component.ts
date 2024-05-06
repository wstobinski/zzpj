import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {Player} from "../../model/player.model";
import {League} from "../../model/league.model";
import {GenerateScheduleDto} from "../../model/DTO/generate-schedule.dto";

@Component({
  selector: 'app-generate-schedule-modal',
  templateUrl: './generate-schedule-modal.component.html',
  styleUrls: ['./generate-schedule-modal.component.scss'],
})
export class GenerateScheduleModalComponent  implements OnInit {


  constructor(private modalController: ModalController,
              private formBuilder: FormBuilder,
              private utils: Utils) { }
  @Input() title: string;
  @Input() league: League;
  hasUnsavedChanges: boolean = false;
  scheduleFormGroup: FormGroup;
  weekdays = ['Niedziela', 'Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek', 'Sobota'];
  ngOnInit() {
    const date = new Date(this.league.startDate);
    const weekdayNumber = date.getDay();
    const weekdayName = this.weekdays[weekdayNumber];
    this.scheduleFormGroup = this.formBuilder.group({
      startDate: [this.league.startDate, [Validators.required]],
      defaultHour: ["19:00", [Validators.required, this.utils.hourValidator]],
      defaultDay: [this.getDayOfWeek(weekdayName), [Validators.required]]

  });
  }

  onClose() {
    if (this.hasUnsavedChanges) {
      this.utils.presentYesNoActionSheet(`Czy na pewno chcesz anulować generowanie harmonogramu? Masz niezapisane zmiany`, 'Tak', 'Nie',
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
    return this.utils.formHasError(this.scheduleFormGroup, fieldName, errorType)
  }

  // onPlayerEdit() {
  //
  //   if (this.mode === 'EDIT') {
  //     this.player = Object.assign(this.player, this.playerFormGroup.value);
  //   } else {
  //     this.player = Object.assign(new Player(), this.playerFormGroup.value);
  //   }
  //   this.modalController.dismiss(this.player, this.mode);
  // }
  onLeagueScheduleSubmit() {
    console.log(this.scheduleFormGroup);
    const generateScheduleDto = Object.assign(new GenerateScheduleDto(), this.scheduleFormGroup.value);
    this.modalController.dismiss(generateScheduleDto, 'submit');
  }

  onStartDateChanged($event: any) {
    console.log($event);
    const date = new Date($event.detail.value); // Current date
    const weekdayNumber = date.getDay();
    const weekdayName = this.weekdays[weekdayNumber];
    this.scheduleFormGroup.get('defaultDay').setValue(this.getDayOfWeek(weekdayName));
  }

  getDayOfWeek(weekday: string) {

    switch (weekday) {
      case 'Niedziela':
        return 'SUNDAY';
      case 'Poniedziałek':
        return 'MONDAY';
      case 'Wtorek':
        return "TUESDAY";
      case 'Środa':
        return "WEDNESDAY";
      case 'Czwartek':
        return 'THURSDAY';
      case 'Piątek':
        return 'FRIDAY';
      case 'Sobota':
        return 'SATURDAY';
      default:
        return 'SUNDAY'
    }

  }
}
