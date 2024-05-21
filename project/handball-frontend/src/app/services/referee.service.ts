import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {Player} from "../model/player.model";
import {Referee} from "../model/referee.model";
import {User} from "../model/user.model";

@Injectable({
  providedIn: 'root'
})
export class RefereeService {

  constructor(private apiService: ApiService) { }


  async getAllReferees(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/referees", {});

  }

  async updateReferee(referee: Referee): Promise<ApiResponse> {
    return await this.apiService.put<ApiResponse>(`/referees/${referee.uuid}`, referee);
  }

  async addReferee(referee: Referee): Promise<ApiResponse> {

    return await this.apiService.post('/referees', referee);

  }

  async deleteReferee(refereeId: number): Promise<ApiResponse> {
    return await this.apiService.delete(`/referees/${refereeId}`);
  }


}
