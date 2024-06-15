import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RefereesPageRoutingModule } from './referees-routing.module';

import { RefereesPage } from './referees.page';
import {HandballComponentsModule} from "../../handball-components.module";
import {RefereeRatingPipe} from "../../pipes/referee-rating.pipe";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        RefereesPageRoutingModule,
        HandballComponentsModule,
        RefereeRatingPipe
    ],
  declarations: [RefereesPage]
})
export class RefereesPageModule {}
