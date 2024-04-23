import { Component } from '@angular/core';
import {Storage} from "@ionic/storage";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent {
  constructor(private storage: Storage) {
    this.initApp();
  }

  initApp() {
  this.initStorage().then(r => {
    console.log("Storage init!");
  })


  }

  async initStorage() {
    await this.storage.create();
  }
}
