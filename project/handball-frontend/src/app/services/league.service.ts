import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {League} from "../model/league.model";
import {GenerateScheduleDto} from "../model/DTO/generate-schedule.dto";

@Injectable({
  providedIn: 'root'
})
export class LeagueService {

  constructor(private apiService: ApiService) { }

  async getAllLeagues(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/leagues");

  }
  async getLeagueById(leagueId: string): Promise<ApiResponse> {
    return await this.apiService.get<ApiResponse>(`/leagues/${leagueId}`);
  }

  async createLeague(league: League): Promise<ApiResponse> {
    return await this.apiService.post<ApiResponse>('/leagues', league);
  }

  async updateLeague(league: League): Promise<ApiResponse> {
    return await this.apiService.put<ApiResponse>(`/leagues/${league.uuid}`, league);
  }

  async deleteLeague(uuid: number) {
    return await this.apiService.delete<ApiResponse>(`/leagues/${uuid}`);
  }

  async finishLeague(uuid: number) {
    return await this.apiService.patch<ApiResponse>(`/leagues/finish/${uuid}`, null);
  }


  async generateSchedule(leagueUuid: number, dto: GenerateScheduleDto): Promise<ApiResponse> {
    return await this.apiService.post<ApiResponse>(`/leagues/${leagueUuid}/generate-schedule`, dto);
  }

  async getRounds(leagueUuid: number): Promise<ApiResponse> {
    return await this.apiService.get<ApiResponse>(`/leagues/${leagueUuid}/rounds`);
  }

  async getMatches(leagueUuid: number): Promise<ApiResponse> {
    return await this.apiService.get<ApiResponse>(`/leagues/${leagueUuid}/matches`);
  }



}
