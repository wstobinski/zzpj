import { Component } from '@angular/core';
import {Storage} from "@ionic/storage";
import {AuthService} from "./services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  constructor(private storage: Storage,
              private authService: AuthService) {
    this.initApp();
  }

  async initApp() {
    await this.initStorage();
    console.log("Storage init!");

    // Wait for the completion of getUserAuthData() before proceeding
    await this.authService.getUserAuthData();

    // Continue with further initialization logic here
  }

  async initStorage() {
    await this.storage.create();
  }
}
