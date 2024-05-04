import {NgModule} from "@angular/core";
import {MainMenuComponent} from "./components/main-menu/main-menu.component";
import {IonicModule} from "@ionic/angular";
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TopToolbarComponent} from "./components/top-toolbar/top-toolbar.component";
import {EditTeamModalComponent} from "./components/edit-team-modal/edit-team-modal.component";
import {ActionMenuPopoverComponent} from "./components/action-menu-popover/action-menu-popover.component";
import {EditPlayerModalComponent} from "./components/edit-player-modal/edit-player-modal.component";
import {TeamManagementComponent} from "./components/team-management/team-management.component";
import {LeagueManagementComponent} from "./components/league-management/league-management.component";

@NgModule({
  declarations: [MainMenuComponent, TopToolbarComponent, EditTeamModalComponent,
    EditPlayerModalComponent, ActionMenuPopoverComponent, TeamManagementComponent,
    LeagueManagementComponent],
  exports: [MainMenuComponent, TopToolbarComponent, EditTeamModalComponent,
    EditPlayerModalComponent, ActionMenuPopoverComponent, TeamManagementComponent,
    LeagueManagementComponent],
  imports: [
    CommonModule,
    IonicModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class HandballComponentsModule {

}
