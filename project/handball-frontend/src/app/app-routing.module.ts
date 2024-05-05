import {NgModule} from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import {AuthenticatedGuard} from "./guards/authenticated.guard";
import {NotFoundComponent} from "./components/not-found/not-found.component";

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./pages/home/home.module').then(m => m.HomePageModule)
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then( m => m.LoginPageModule)
  },
  {
    path: 'teams',
    loadChildren: () => import('./pages/teams/teams.module').then( m => m.TeamsPageModule),
    canActivate: [AuthenticatedGuard]
  },
  {
    path: 'players',
    loadChildren: () => import('./pages/players/players.module').then( m => m.PlayersPageModule),
    canActivate: [AuthenticatedGuard]
  },
  {
    path: 'account',
    loadChildren: () => import('./pages/account/account.module').then( m => m.AccountPageModule),
    canActivate: [AuthenticatedGuard]
  },
  {
    path: 'leagues',
    loadChildren: () => import('./pages/leagues/leagues.module').then( m => m.LeaguesPageModule),
    canActivate: [AuthenticatedGuard]
  },
  {
    path: 'league-panel/:leagueId',
    loadChildren: () => import('./pages/league-panel/league-panel.module').then( m => m.LeaguePanelPageModule),
    canActivate: [AuthenticatedGuard]
  },
  { path: '404',
    component: NotFoundComponent },
  { path: '**',
    redirectTo: '/404' }






];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules})
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
