import {Component, Input, OnInit} from '@angular/core';
import {User} from "../../model/user.model";
import {UserService} from "../../services/user.service";
import {Subscription} from "rxjs";
import {Utils} from "../../utils/utils";
import {Router} from "@angular/router";

@Component({
  selector: 'app-top-toolbar',
  templateUrl: './top-toolbar.component.html',
  styleUrls: ['./top-toolbar.component.scss'],
})
export class TopToolbarComponent  implements OnInit {
  @Input() title: string;
  user: User;
  userSub: Subscription;
  constructor(private userService: UserService,
              private router: Router) { }

  ngOnInit() {

    this.userSub = this.userService.getUser().subscribe(user => {
      this.user = user;
    })
  }

  logout() {

  }

  goTo(pageName: string) {
    this.router.navigateByUrl(pageName);
  }
}
