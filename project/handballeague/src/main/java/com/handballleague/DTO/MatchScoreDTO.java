package com.handballleague.DTO;

import lombok.Data;

public class MatchScoreDTO {
    @Data
    public static class MatchResultDto {
        private TeamScoreDto team1Score;
        private TeamScoreDto team2Score;
    }

    @Data
    public static class TeamScoreDto {
        private Long teamId;
        private int goals;
        private int lostGoals;
        private int fouls;
        private int ballPossession;
        private int yellowCards;
        private int redCards;
        private int timePenalties;
    }

}
