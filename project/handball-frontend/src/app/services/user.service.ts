import { Injectable } from '@angular/core';
import {BehaviorSubject, firstValueFrom, Observable} from "rxjs";
import {User} from "../model/user.model";
import {Utils} from "../utils/utils";
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: BehaviorSubject<User> = new BehaviorSubject<User>(null);

  constructor(private utils: Utils,
              private apiService: ApiService) {
    console.log('user init')
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

  async login(loginData: { email: string, password: string }): Promise<ApiResponse> {
      return await this.apiService.post("/users/login", loginData);

  }

  async updateUser(userId: number, user: User): Promise<ApiResponse> {
    return await this.apiService.put(`/users/${userId}`, user);
  }

}
