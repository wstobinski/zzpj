import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {PlayersService} from "./players.service";
import {ApiResponse} from "../model/ApiResponse";
import {Team} from "../model/team.model";
import {Player} from "../model/player.model";
import {Match} from "../model/match.model";
import {MatchScoreDto} from "../model/DTO/match-score.dto";

@Injectable({
  providedIn: 'root'
})
export class MatchService {

  constructor(private apiService: ApiService) { }


  async getAllMatches(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/matches", {});

  }

  async updateMatch(match: Match): Promise<ApiResponse> {
    return await this.apiService.put<ApiResponse>(`/matches/${match.uuid}`, match);
  }

  async completeMatch(match: Match, matchScoreDto: MatchScoreDto): Promise<ApiResponse> {
    return await this.apiService.post<ApiResponse>(`/matches/${match.uuid}/finish-match`, matchScoreDto);
  }

  async getMatchScores(match:Match): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>(`/matches/score/${match.uuid}`);

  }


}
