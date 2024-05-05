import {Team} from "./team.model";
import {League} from "./league.model";

export class TeamContest {
  public team: Team;
  public league: League;
  public points: number;
  public goalsAcquired: number;
  public goalsLost: number;
  public gamesPlayed: number;
  public wins: number;
  public draws: number;
  public losses: number;
}
