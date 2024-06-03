import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {Post} from "../model/post.model";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private apiService: ApiService) { }


  async getAllPosts(): Promise<ApiResponse> {

    return await this.apiService.get<ApiResponse>("/posts");

  }

  async addNewPost(postData: Post): Promise<ApiResponse> {
    return await this.apiService.post<ApiResponse>("/posts", postData);
  }

  async editPost(postData: Post): Promise<ApiResponse> {
    return await this.apiService.put<ApiResponse>(`/posts/${postData.uuid}`, postData);
  }

  async deletePost(postId: number): Promise<ApiResponse> {
    return await this.apiService.delete<ApiResponse>(`/posts/${postId}`);
  }
}
