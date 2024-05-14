import {Match} from "./match.model";

export class Round {
  uuid: number;
  number: number;
  startDate: Date;
  matches: Match[];
}
