import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import {HandballComponentsModule} from "./handball-components.module";
import {IonicStorageModule} from "@ionic/storage-angular";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {LoadingInterceptorService} from "./services/loading-interceptor.service";
import {registerLocaleData} from "@angular/common";
import localePl from '@angular/common/locales/pl';


registerLocaleData(localePl);

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule,
    IonicModule.forRoot(),
    AppRoutingModule,
    HandballComponentsModule,
    HttpClientModule,
    IonicStorageModule.forRoot({
      name: '__handball_league'
    }),],
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy},
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LoadingInterceptorService,
      multi: true
    }],
  bootstrap: [AppComponent],
})
export class AppModule {}
