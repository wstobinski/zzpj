import {Injectable} from "@angular/core";
import { Storage } from '@ionic/storage';
import {FormGroup} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class Utils {

  constructor(private storage: Storage) {
  }

  setStorageObject(key:string, value: any) {
    this.storage.set(key, value);
  }

  async getStorageObject<T>(key: string): Promise<T> {
    const object = await this.storage.get(key);
    return JSON.parse(object);
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

}
