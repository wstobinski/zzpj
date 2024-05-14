import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";

@Injectable({
  providedIn: 'root'
})
export class TeamContestService {

  constructor(private apiService: ApiService) {
  }


  async getForLeague(leagueId: number): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>(`/team-contests/for-league/${leagueId}`, {});

  }

}
