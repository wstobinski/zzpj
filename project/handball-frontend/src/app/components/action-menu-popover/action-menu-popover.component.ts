import {Component, Input, OnInit} from '@angular/core';
import {ActionButton} from "../../model/action-button.model";

@Component({
  selector: 'app-action-menu-popover',
  templateUrl: './action-menu-popover.component.html',
  styleUrls: ['./action-menu-popover.component.scss'],
})
export class ActionMenuPopoverComponent  implements OnInit {

  @Input() actionButtons : ActionButton[];
  @Input() actionObject: any;

  constructor() { }

  ngOnInit() {}

  onOptionClick($index: number) {
    console.log(this.actionButtons)
    this.actionButtons[$index].buttonAction(this.actionObject);
  }
}
