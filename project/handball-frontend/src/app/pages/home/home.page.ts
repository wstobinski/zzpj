import {Component, OnDestroy, OnInit} from '@angular/core';
import {GenericPage} from "../generic/generic.page";
import {UserService} from "../../services/user.service";
import {LoadingService} from "../../services/loading.service";
import {PopoverController} from "@ionic/angular";
import {User} from "../../model/user.model";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage extends GenericPage implements OnInit, OnDestroy {

  constructor(private userService: UserService,
              loadingService: LoadingService,
              popoverController: PopoverController) {
    super(loadingService, popoverController);
  }

  user: User;
  userSub: Subscription

  override ngOnInit() {
    super.ngOnInit();
    this.userSub = this.userService.getUser().subscribe(u => {
      this.user = u;
      console.log("USER", u)
    });
  }

  override ngOnDestroy() {
    super.ngOnDestroy();
    if (this.userSub) {
      this.userSub.unsubscribe();
    }
  }
}
