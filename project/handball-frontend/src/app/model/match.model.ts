import {Team} from "./team.model";
import {Referee} from "./referee.model";

export class Match {
  public uuid: number;
  public gameDate: Date;
  public homeTeam: Team;
  public awayTeam: Team;
  public referee: Referee;
  public finished: boolean;
  public homeTeamScore?: number;
  public awayTeamScore?: number;
  public canComment?: boolean;
  public canEdit?: boolean;
}
