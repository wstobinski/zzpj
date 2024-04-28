import {NgModule} from "@angular/core";
import {MainMenuComponent} from "./components/main-menu/main-menu.component";
import {IonicModule} from "@ionic/angular";
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TopToolbarComponent} from "./components/top-toolbar/top-toolbar.component";

@NgModule({
  declarations: [MainMenuComponent, TopToolbarComponent],
  exports: [MainMenuComponent, TopToolbarComponent],
  imports: [
    CommonModule,
    IonicModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class HandballComponentsModule {

}
