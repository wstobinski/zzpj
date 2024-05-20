export interface ActionButton {
  buttonName: string | ((object: any) => string);
  buttonAction: any;
  actionColor?: string;
  displayCondition?: any;
}
