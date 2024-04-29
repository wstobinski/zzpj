import { Component, OnInit } from '@angular/core';
import {TeamsService} from "../../services/teams.service";

@Component({
  selector: 'app-teams',
  templateUrl: './teams.page.html',
  styleUrls: ['./teams.page.scss'],
})
export class TeamsPage implements OnInit {

  constructor(private teamsService: TeamsService) { }

  ngOnInit() {
  }

  onTestClick() {
    this.teamsService.getAllTeams().then(r => {
      console.log(r)
    });
  }
}
