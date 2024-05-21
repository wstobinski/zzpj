import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RefereesPageRoutingModule } from './referees-routing.module';

import { RefereesPage } from './referees.page';
import {HandballComponentsModule} from "../../handball-components.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        RefereesPageRoutingModule,
        HandballComponentsModule
    ],
  declarations: [RefereesPage]
})
export class RefereesPageModule {}
