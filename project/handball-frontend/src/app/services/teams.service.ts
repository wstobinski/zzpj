import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";

@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  constructor(private apiService: ApiService) { }


  async getAllTeams(): Promise<any> {

    return await this.apiService.get<ApiResponse>("/teams", {});

  }

}
