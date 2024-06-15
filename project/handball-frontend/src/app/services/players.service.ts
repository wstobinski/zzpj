import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {Player} from "../model/player.model";
import {Team} from "../model/team.model";

@Injectable({
  providedIn: 'root'
})
export class PlayersService {

  constructor(private apiService: ApiService) { }


  async getAllPlayers(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/players", {});

  }

  async getFreeAgents(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/players/free-agents", {});

  }

  async deletePlayer(playerId: number): Promise<ApiResponse> {
    return await this.apiService.delete(`/players/${playerId}`);
  }

  async updatePlayer(player: Player): Promise<ApiResponse> {
    return await this.apiService.put<ApiResponse>(`/players/${player.uuid}`, player);
  }

  async addPlayer(player: Player): Promise<ApiResponse> {

    return await this.apiService.post('/players', player);

  }

  async generatePlayers(body: {nationality: string, numberOfPlayers: number}): Promise<ApiResponse> {
    return await this.apiService.post('/players/generate-players', body);
  }
}
