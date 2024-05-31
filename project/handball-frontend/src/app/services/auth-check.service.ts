import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {from, Observable} from "rxjs";
import {ApiResponse} from "../model/ApiResponse";

@Injectable({
  providedIn: 'root'
})
export class AuthCheckService {



  constructor(private apiService: ApiService) { }

  async isCaptainOfTeam(teamId: number): Promise<ApiResponse> {
    return await this.apiService.get<ApiResponse>(`/auth-check/is-captain-of-team/${teamId}`);
  }

  async isCaptainInMatch(matchId: number): Promise<ApiResponse> {
    return await this.apiService.get<ApiResponse>(`/auth-check/is-captain-in-match/${matchId}`);
  }

  async isRefereeInMatch(matchId: number): Promise<ApiResponse> {
    return await this.apiService.get<ApiResponse>(`/auth-check/is-referee-in-match/${matchId}`);
  }
}
