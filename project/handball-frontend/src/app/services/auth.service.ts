import {Injectable} from '@angular/core';
import {BehaviorSubject, map} from "rxjs";
import {UserAuthData} from "../model/UserAuthData";
import {Utils} from "../utils/utils";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private utils: Utils,
  ) {
  }

  private _userAuthData = new BehaviorSubject<UserAuthData>(null);

  get userAuthData() {
    return this._userAuthData.asObservable();
  }

  async getUserAuthData() {
    if (this._userAuthData.getValue()) {
      console.log('Auth from Behavior Subject')
      return this._userAuthData.asObservable();
    } else {
      const userAuthData = await this.utils.getStorageObject<UserAuthData>('userAuthData');
      if (userAuthData) {
        console.log('Auth from localStorage')
        this._userAuthData.next(userAuthData);
        return this._userAuthData;
      } else {
        return this._userAuthData;
      }
    }
  }

  get token() {
    return this.userAuthData.pipe(map(userAuthData => {
      return userAuthData ? userAuthData.token : null;
    }));
  }

}
