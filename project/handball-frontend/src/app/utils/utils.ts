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
    const passwordConfirm = control.get('passwordConfirm');

    return hour && hourRegex.test(hour)
      ? null
      : {hour: true};
  };

}
