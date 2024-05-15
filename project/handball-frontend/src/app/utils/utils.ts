import {Injectable} from "@angular/core";
import { Storage } from '@ionic/storage';
import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";
import {ActionSheetController, ToastController} from "@ionic/angular";

@Injectable({
  providedIn: 'root'
})
export class Utils {

  constructor(private storage: Storage,
              private toastController: ToastController,
              private actionSheetController: ActionSheetController) {
  }

  setStorageObject(key:string, value: any) {
    this.storage.set(key, value);
  }

  async getStorageObject<T>(key: string): Promise<T> {
    const object = await this.storage.get(key);
    if (typeof object === "string") {
      return JSON.parse(object);
    }
    return object;
  }

  typeCopy<T>(target: T, src) {
    return Object.assign(target, src);
  }


  removeStorageObject(key: string): Promise<any> {
    return this.storage.remove(key);
  }

  formHasError(form: FormGroup, fieldName: string, errorType: string) {
    return form?.get(fieldName)?.hasError(errorType) && form?.get(fieldName)?.touched;
  }

  async presentAlertToast(message: string, duration: number = 4000) {
    const toast = await this.toastController.create({
      header: message,
      position: 'bottom',
      color: 'danger',
      duration: duration,
      buttons: [{
        text: 'Cancel',
        role: 'cancel',
        handler: () => {
        }
      }
      ]
    });
    toast.present();
  }

  async presentInfoToast(message: string, duration: number = 4000) {
    const toast = await this.toastController.create({
      header: message,
      position: 'bottom',
      duration: duration,
      buttons: [{
        text: 'Cancel',
        role: 'cancel',
        handler: () => {
        }
      }
      ]
    });
    toast.present();
  }

  async presentYesNoActionSheet(title: string, yesButtonTitle: string, noButtonTitle: string, yesHandler: any, noHandler: any) {
    const actionSheetOptions = {
      header: title,
      buttons: [{
        text: yesButtonTitle,
        role: 'destructive',
        icon: 'checkmark-outline',
        handler: yesHandler
      }, {
        text: noButtonTitle,
        role: 'destructive',
        icon: 'close-outline',
        handler: noHandler
      }]
    };


    const actionSheet = await this.actionSheetController.create(actionSheetOptions);
    await actionSheet.present();
  }

  passwordMismatchValidator: ValidatorFn = (
    control: AbstractControl,
  ): ValidationErrors | null => {
    const password = control.get('password');
    const passwordConfirm = control.get('passwordConfirm');

    return password && passwordConfirm && password.value !== passwordConfirm.value
      ? { passwordMismatch: true }
      : null;
  };

  hourValidator: ValidatorFn = (
    control: AbstractControl,
  ): ValidationErrors | null => {
    const hourRegex = new RegExp('^([01][0-9]|2[0-3]):[0-5][0-9]$');
    const hour = control.value;


    return hour && hourRegex.test(hour)
      ? null
      : {hour: true};
  };

  oneToHundredValidator: ValidatorFn = (
    control: AbstractControl,
  ): ValidationErrors | null => {
    const oneToHundredRegex: RegExp = new RegExp('^([1-9][0-9]?|100)$');
    const value = control.value;

    if (!value || value.length === 0) {
      return null;
    }

    return value && oneToHundredRegex.test(value)
      ? null
      : {oneToHundred: true};
  };

  rangeValidator(min: number, max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (value && value.length >= min && value.length <= max) {
        return null;
      }
      return { rangeError: `Selection must be between ${min} and ${max} items` };
    };
  }

}
