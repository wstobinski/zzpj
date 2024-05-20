import {Component, Input, OnInit} from '@angular/core';
import {ActionButton} from "../../model/action-button.model";
import {PopoverController} from "@ionic/angular";

@Component({
  selector: 'app-action-menu-popover',
  templateUrl: './action-menu-popover.component.html',
  styleUrls: ['./action-menu-popover.component.scss'],
})
export class ActionMenuPopoverComponent  implements OnInit {

  @Input() actionButtons : ActionButton[];
  @Input() actionObject: any;

  constructor(private popoverController: PopoverController) { }

  ngOnInit() {}

  async onOptionClick(index: number) {
    const buttonAction = this.actionButtons[index].buttonAction;
    if (buttonAction) {
      await buttonAction(this.actionObject);
    }
    await this.popoverController.dismiss();
  }

  isFunction(value: any): value is Function {
    return typeof value === 'function';
  }
}
