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

  initApp() {
  this.initStorage().then(r => {
    console.log("Storage init!");
    this.authService.getUserAuthData()
  })


  }

  async initStorage() {
    await this.storage.create();
  }
}
