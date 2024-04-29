import {Injectable} from "@angular/core";
import { Storage } from '@ionic/storage';
import {FormGroup} from "@angular/forms";
import {ToastController} from "@ionic/angular";

@Injectable({
  providedIn: 'root'
})
export class Utils {

  constructor(private storage: Storage,
              private toastController: ToastController) {
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
    return form.get(fieldName)?.hasError(errorType) && form.get(fieldName)?.touched;
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

}
