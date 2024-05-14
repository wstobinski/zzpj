import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { LeaguePanelPageRoutingModule } from './league-panel-routing.module';

import { LeaguePanelPage } from './league-panel.page';
import {HandballComponentsModule} from "../../handball-components.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        LeaguePanelPageRoutingModule,
        HandballComponentsModule
    ],
  declarations: [LeaguePanelPage]
})
export class LeaguePanelPageModule {}
