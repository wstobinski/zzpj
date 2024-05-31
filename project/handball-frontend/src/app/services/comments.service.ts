import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {ApiResponse} from "../model/ApiResponse";
import {CommentDto} from "../model/DTO/comment.dto";

@Injectable({
  providedIn: 'root'
})
export class CommentsService {

  constructor(private apiService: ApiService) {
  }

  async addComment(commentData: CommentDto): Promise<ApiResponse> {
    return await this.apiService.post<ApiResponse>("/comments", commentData);
  }
}
