import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {User} from "../model/user.model";
import {Utils} from "../utils/utils";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: BehaviorSubject<User> = new BehaviorSubject<User>(null);

  constructor(private utils: Utils) {
    this.initUser();
  }

  private async initUser() {
    const storedUser = await this.utils.getStorageObject<User>('user');
    if (storedUser) {
      this.user.next(storedUser);
    }
  }


  getUser(): Observable<User> {
    return this.user.asObservable();
  }

  setUser(user: User) {
    this.user.next(user);
    this.storeUserData(user);
  }

  private storeUserData(user: User) {
    this.utils.setStorageObject('user', user);
  }
}
