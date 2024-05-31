import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { AccountPageRoutingModule } from './account-routing.module';

import { AccountPage } from './account.page';
import {HandballComponentsModule} from "../../handball-components.module";
import {RoleTranslatePipe} from "../../pipes/role-translate.pipe";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        AccountPageRoutingModule,
        HandballComponentsModule,
        ReactiveFormsModule,
        RoleTranslatePipe
    ],
  declarations: [AccountPage]
})
export class AccountPageModule {}
