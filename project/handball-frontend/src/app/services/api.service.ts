import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {catchError} from 'rxjs/operators';
import {AuthService} from "./auth.service";
import {UserAuthData} from "../model/UserAuthData";
import {firstValueFrom, Observable, throwError} from "rxjs";
import {environment} from "../../environments/environment";
import {ApiResponse} from "../model/ApiResponse";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient,
              private authService: AuthService) { }

  async get<ApiResponse>(url: string, options?: {
    headers?: HttpHeaders;
    params?: HttpParams
  }): Promise<ApiResponse> {

    try {
      await this.addAuthHeader(options);
      return await firstValueFrom(this.http.get<ApiResponse>(`${environment.API_URL}${url}`, options));
    } catch (error) {
      console.error(error);
      throw error;
    }
  }

  async post(url: string, body: any | null, options?: {
    headers?: HttpHeaders;
    params?: HttpParams
  }): Promise<ApiResponse> {
    try {
      await this.addAuthHeader(options);
      return await firstValueFrom(this.http.post<ApiResponse>(`${environment.API_URL}${url}`, body, options));
    } catch (error) {
      console.error(error);
      throw error;
    }
  }

  async put<ApiResponse>(url: string, body: any | null, options?: {
    headers?: HttpHeaders;
    params?: HttpParams
  }): Promise<ApiResponse> {

    try {
      await this.addAuthHeader(options);
      return await firstValueFrom(this.http.put<ApiResponse>(`${environment.API_URL}${url}`, body, options));
    } catch (error) {
      console.error(error);
      throw error;
    }
  }

  async delete<ApiResponse>(url: string, options?: {
    headers?: HttpHeaders;
    params?: HttpParams
  }): Promise<ApiResponse> {

    try {
      await this.addAuthHeader(options);
      return await firstValueFrom(this.http.delete<ApiResponse>(`${environment.API_URL}${url}`, options));
    } catch (error) {
      console.error(error);
      throw error;
    }
  }

  async addAuthHeader(options: {
    headers?: HttpHeaders;
    params?: HttpParams
  }) {
    const userAuthData: UserAuthData = await firstValueFrom(this.authService.userAuthData);
    if (userAuthData && userAuthData.token) {
      if (!options) {
        options = {headers: new HttpHeaders().append('token', userAuthData.token)};
      } else {
        options.headers = new HttpHeaders().append('token', userAuthData.token);
      }
    }
    return options;
  }

}