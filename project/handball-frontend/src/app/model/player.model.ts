export class Player {
  public uuid: number;
  public firstName: string;
  public lastName: string;
  public phoneNumber: string;
  public pitchNumber: number;
  public suspended: boolean;
  public captain: boolean;
  public email?: string;

  public constructor(init?: Partial<Player>) {
    Object.assign(this, init);
  }
}
