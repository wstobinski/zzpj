import { Injectable } from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {LoadingService} from "./loading.service";
import {finalize, tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LoadingInterceptorService implements HttpInterceptor {

  constructor(private loadingService: LoadingService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    this.loadingService.isLoading.next(true);
    return next.handle(request).pipe(
      finalize(() => {
        this.loadingService.isLoading.next(false);
      })
    );
  }
}
