import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {MenuController} from "@ionic/angular";
import {UserService} from "../../services/user.service";
import {Subscription} from "rxjs";
import {User} from "../../model/user.model";
import {Utils} from "../../utils/utils";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss'],
})
export class MainMenuComponent  implements OnInit {

  constructor(private router: Router,
              private menu: MenuController,
              private userService: UserService,
              private utils: Utils) { }

  user: User;
  userSub: Subscription;

  ngOnInit() {

    this.userSub = this.userService.getUser().subscribe(user => {
    this.user = user;
    })
  }

  goTo(pageName: string) {
    this.router.navigateByUrl(pageName);
    this.menu.close();
  }

  getLoginPageName() {
    return this.user ? "Account" : "Login";
  }

  getLoginPageIcon() {
    return this.user ? "key-outline" : "log-in-outline";
  }

  logout() {
    this.utils.removeStorageObject('user').then(() => {
      window.location.reload();
    });
  }
}
