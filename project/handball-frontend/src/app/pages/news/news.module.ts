import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { NewsPageRoutingModule } from './news-routing.module';

import { NewsPage } from './news.page';
import {HandballComponentsModule} from "../../handball-components.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        NewsPageRoutingModule,
        HandballComponentsModule
    ],
  declarations: [NewsPage]
})
export class NewsPageModule {}
