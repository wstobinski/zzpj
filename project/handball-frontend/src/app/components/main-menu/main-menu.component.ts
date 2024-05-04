import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {MenuController} from "@ionic/angular";
import {UserService} from "../../services/user.service";
import {Subscription} from "rxjs";
import {User} from "../../model/user.model";
import {Utils} from "../../utils/utils";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss'],
})
export class MainMenuComponent  implements OnInit {

  constructor(private router: Router,
              private menu: MenuController,
              private userService: UserService,
              private authService: AuthService) { }

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

  logout() {
    this.authService.logout();
    this.router.navigateByUrl('/home');
  }
}
