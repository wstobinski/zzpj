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

}
