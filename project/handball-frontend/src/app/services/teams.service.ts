import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {Team} from "../model/team.model";
import {Player} from "../model/player.model";
import {PlayersService} from "./players.service";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  constructor(private apiService: ApiService,
              private playersService: PlayersService) { }


  async getAllTeams(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/teams", {});

  }

  async getFreeAgents(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/teams/free-agents", {});

  }

  async createTeam(team: Team): Promise<ApiResponse> {
    return await this.apiService.post("/teams", team);
  }

  async updateTeam(team: Team): Promise<ApiResponse> {
    return await this.apiService.put<ApiResponse>(`/teams/${team.uuid}`, team);
  }

  async deleteTeam(teamId: number): Promise<ApiResponse> {
    return await this.apiService.delete<ApiResponse>(`/teams/${teamId}`);
  }

  async changeCaptains(newCaptain: Player, oldCaptain: Player): Promise<ApiResponse> {
    return this.playersService.updatePlayer(newCaptain).then(async (r) => {
      if (r.ok) {
        return await this.playersService.updatePlayer(oldCaptain);
      }
      return null;
    });

  }

  async generateTeams(body: {leagueId: number, season: string}): Promise<ApiResponse> {
    return await this.apiService.post('/teams/generate-teams', body);
  }
}
