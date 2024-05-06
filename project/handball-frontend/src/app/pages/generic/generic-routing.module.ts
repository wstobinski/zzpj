import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { GenericPage } from './generic.page';

const routes: Routes = [
  {
    path: '',
    component: GenericPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GenericPageRoutingModule {}
