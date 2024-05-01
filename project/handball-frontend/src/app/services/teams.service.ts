import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {Team} from "../model/team.model";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  constructor(private apiService: ApiService) { }


  async getAllTeams(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/teams", {});

  }

  async deleteTeam(teamId: number): Promise<ApiResponse> {
    return await this.apiService.delete(`/teams/${teamId}`);
  }
}
