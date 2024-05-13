import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {MenuController} from "@ionic/angular";
import {UserService} from "../../services/user.service";
import {Subscription} from "rxjs";
import {User} from "../../model/user.model";
import {Utils} from "../../utils/utils";
import {AuthService} from "../../services/auth.service";
import {LeagueService} from "../../services/league.service";
import {League} from "../../model/league.model";

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.scss'],
})
export class MainMenuComponent  implements OnInit {

  constructor(private router: Router,
              private menu: MenuController,
              private userService: UserService,
              private authService: AuthService,
              private leagueService: LeagueService) { }

  user: User;
  userSub: Subscription;
  leagues: League[];

  ngOnInit() {

    this.userSub = this.userService.getUser().subscribe(user => {
    this.user = user;
    });
    this.leagueService.getAllLeagues().then(r => {
      if (r.ok) {
        const allLeagues = r.response as League[];
        this.leagues = allLeagues.filter(l => {
          return l.scheduleGenerated && !l.finishedDate
        });
      }
    });
  }

  goTo(pageName: string) {
    this.router.navigateByUrl(pageName);
    this.menu.close();
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl('/home');
  }

  goToLeaguePanel(league: League) {
    this.router.navigate(['league-panel', league.uuid]);
    this.menu.close();
  }
}
