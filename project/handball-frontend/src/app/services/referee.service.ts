import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";

@Injectable({
  providedIn: 'root'
})
export class RefereeService {

  constructor(private apiService: ApiService) { }


  async getAllReferees(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/referees", {});

  }
}
