import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LeaguePanelPage } from './league-panel.page';

const routes: Routes = [
  {
    path: '',
    component: LeaguePanelPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LeaguePanelPageRoutingModule {}
