import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RefereesPage } from './referees.page';

const routes: Routes = [
  {
    path: '',
    component: RefereesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RefereesPageRoutingModule {}
