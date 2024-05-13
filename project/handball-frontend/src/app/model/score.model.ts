import {Match} from "./match.model";
import {Team} from "./team.model";

export class Score {
  public uuid: number;
  public match: Match;
  public teamId: Team;
  public goals: number;
  public lostGoals: number;
  public ballPossession: number;
  public yellowCards: number;
  public redCards: number;
  public fouls: number;
  public timePenalties: number;
}
