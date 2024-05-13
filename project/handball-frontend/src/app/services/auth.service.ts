import {Injectable} from '@angular/core';
import {BehaviorSubject, map, take} from "rxjs";
import {UserAuthData} from "../model/UserAuthData";
import {Utils} from "../utils/utils";
import {jwtDecode} from "jwt-decode";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private utils: Utils,
              private router: Router
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

  setUserAuthData(token: string) {
    const decoded : {
      role: string,
      iat: number,
      exp: number
    } = jwtDecode(token);
    console.log(decoded)
    const userAuthData: UserAuthData = {
      token: token,
      role: decoded.role
    };
    this._userAuthData.next(userAuthData);
    this.storeUserAuthData(userAuthData);
  }
  private storeUserAuthData(userAuthData: UserAuthData) {
    this.utils.setStorageObject('userAuthData', userAuthData);
  }

  get token() {
    return this.userAuthData.pipe(map(userAuthData => {
      return userAuthData ? userAuthData.token : null;
    }));
  }

  get isAuthenticated() {
    return this.userAuthData.pipe(
      map(userAuthData => {
        if (userAuthData) {
          return !!userAuthData.token;
        } else {
          return false;
        }
      })
    );
  }

  async manualLoginCheck() {
    console.log('manualLoginCheck');
    const userAuthData: UserAuthData = await this.utils.getStorageObject('userAuthData');
    this._userAuthData.next(userAuthData);
    return !!userAuthData;
  }


  get decodedToken(): any {
    return this.userAuthData.pipe(
      map(userAuthData => {
        if (userAuthData) {
          return jwtDecode(userAuthData.token);
        } else {
          return null;
        }
      })
    );
  }

  logout(redirectPath: string = null) {
    this.utils.removeStorageObject('userAuthData').then(() => {
      this.utils.removeStorageObject('user').then(() => {
        window.location.reload();
        if (redirectPath) {
          this.router.navigateByUrl(redirectPath);
        }
      });
    });
  }

}
