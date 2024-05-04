import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LeaguesPage } from './leagues.page';

const routes: Routes = [
  {
    path: '',
    component: LeaguesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LeaguesPageRoutingModule {}
