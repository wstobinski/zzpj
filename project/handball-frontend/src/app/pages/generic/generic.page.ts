import {Component, OnDestroy, OnInit} from '@angular/core';
import {LoadingService} from "../../services/loading.service";
import {Subscription} from "rxjs";
import {ActionMenuPopoverComponent} from "../../components/action-menu-popover/action-menu-popover.component";
import {PopoverController} from "@ionic/angular";
import {ActionButton} from "../../model/action-button.model";

@Component({
  selector: 'app-generic',
  templateUrl: './generic.page.html',
  styleUrls: ['./generic.page.scss'],
})
export class GenericPage implements OnInit, OnDestroy {

  constructor(private loadingService: LoadingService,
              private popoverController: PopoverController) { }

  isLoading: boolean = false;
  hasUnsavedChanges: boolean = false;
  loadingSub: Subscription;
  ngOnInit() {
    this.loadingSub = this.loadingService.isLoading.subscribe(isLoading => {
      this.isLoading = isLoading;
    })
  }

  ngOnDestroy(): void {
    if (this.loadingSub) {
      this.loadingSub.unsubscribe();
    }
  }

  async openPopover(ev: any, object: any, actionButtons: ActionButton[]) {
    const popover = await this.popoverController.create({
      component: ActionMenuPopoverComponent,
      componentProps: {
        actionButtons: actionButtons,
        actionObject: object
      },
      event: ev,
      translucent: true,
    });
    return await popover.present();
  }

}
