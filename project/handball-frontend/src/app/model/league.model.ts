import {Team} from "./team.model";

export class League {
  public uuid: number;
  public name: string;
  public teams: Team[];
  public startDate: Date;
}
