package com.handballleague.DTO;

import lombok.Data;

@Data
public class GenerateTeamsDTO {
    String leagueId;
    String season;
    boolean generatePlayers;
}
