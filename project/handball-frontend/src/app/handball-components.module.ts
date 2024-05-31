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
import {GenerateScheduleModalComponent} from "./components/generate-schedule-modal/generate-schedule-modal.component";
import {NotFoundComponent} from "./components/not-found/not-found.component";
import {MatchResultComponent} from "./components/match-result/match-result.component";
import {MatchEditModalComponent} from "./components/match-edit-modal/match-edit-modal.component";
import {EditRefereeModalComponent} from "./components/edit-referee-modal/edit-referee-modal.component";
import {RefereeCommentModalComponent} from "./components/referee-comment-modal/referee-comment-modal.component";
import {PostModalComponent} from "./components/post-modal/post-modal.component";

@NgModule({
  declarations: [MainMenuComponent, TopToolbarComponent, EditTeamModalComponent,
    EditPlayerModalComponent, ActionMenuPopoverComponent, TeamManagementComponent,
    LeagueManagementComponent, GenerateScheduleModalComponent, NotFoundComponent,
    MatchResultComponent, MatchEditModalComponent, EditRefereeModalComponent,
    RefereeCommentModalComponent, PostModalComponent],
  exports: [MainMenuComponent, TopToolbarComponent, EditTeamModalComponent,
    EditPlayerModalComponent, ActionMenuPopoverComponent, TeamManagementComponent,
    LeagueManagementComponent, GenerateScheduleModalComponent, NotFoundComponent,
    MatchResultComponent, MatchEditModalComponent, EditRefereeModalComponent,
    RefereeCommentModalComponent, PostModalComponent],
  imports: [
    CommonModule,
    IonicModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class HandballComponentsModule {

}
