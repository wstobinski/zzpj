package com.handballleague.integrationTests;

import com.handballleague.DTO.GenerateScheduleDTO;
import com.handballleague.DTO.MatchScoreDTO;
import com.handballleague.model.*;
import com.handballleague.repositories.RefereeRepository;
import com.handballleague.services.LeagueService;
import com.handballleague.services.MatchService;
import com.handballleague.services.TeamContestService;
import com.handballleague.services.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TeamContestIntegrationTest {

    @Autowired
    private RefereeRepository refereeRepository;
    @Autowired
    private TeamService teamService;
    @Autowired
    private LeagueService leagueService;

    @Autowired
    private TeamContestService underTestService;

    private League createdLeague;
    @Autowired
    private MatchService matchService;

    @BeforeEach
    public void setup() {
        Team team1 = new Team("Test team");
        Team team2 = new Team("Test team2");
        Team team3 = new Team("Test team3");
        League league = new League("Test league", LocalDateTime.now());
        Referee referee = Referee.builder()
                .uuid(1L)
                .email("referee@test.com")
                .firstName("Sedzia")
                .lastName("Test").build();
        refereeRepository.save(referee);
        // Save Team and League entities to the database
        teamService.create(team1);
        teamService.create(team2);
        teamService.create(team3);
        league.setTeams(List.of(team1, team2, team3));
        createdLeague = leagueService.create(league);
    }

    @Test
    void scheduleGenerationCreatesTeamContests_Test() {

        List<TeamContest> nonExistingTeamContests = underTestService.findTeamContestsInCertainLeague(createdLeague.getUuid());
        assertThat(nonExistingTeamContests.isEmpty()).isTrue();

        leagueService.generateSchedule(createdLeague, GenerateScheduleDTO.builder()
                .startDate(LocalDateTime.now())
                .defaultHour("19:00")
                .defaultDay(DayOfWeek.SUNDAY)
                .build());
        List<TeamContest> existingTeamContests = underTestService.findTeamContestsInCertainLeague(createdLeague.getUuid());
        assertThat(existingTeamContests.size()).isEqualTo(createdLeague.getTeams().size());
        for (TeamContest teamContest : existingTeamContests) {
            assertThat(teamContest.getLeague()).isEqualTo(leagueService.getById(createdLeague.getUuid()));
            assertThat(teamContest.getGamesPlayed()).isEqualTo(0);
            assertThat(teamContest.getGoalsAcquired()).isEqualTo(0);
            assertThat(teamContest.getGoalsLost()).isEqualTo(0);
            assertThat(teamContest.getPoints()).isEqualTo(0);
            assertThat(teamContest.getWins()).isEqualTo(0);
            assertThat(teamContest.getLosses()).isEqualTo(0);
            assertThat(teamContest.getDraws()).isEqualTo(0);
        }
    }

    @Test
    void matchCompletionUpdatesTeamContests_Test() {

        leagueService.generateSchedule(createdLeague, GenerateScheduleDTO.builder()
                .startDate(LocalDateTime.now())
                .defaultHour("19:00")
                .defaultDay(DayOfWeek.SUNDAY)
                .build());

        List<TeamContest> existingTeamContests = underTestService.findTeamContestsInCertainLeague(createdLeague.getUuid());
        assertThat(existingTeamContests.size()).isEqualTo(createdLeague.getTeams().size());
        for (TeamContest teamContest : existingTeamContests) {
            assertThat(teamContest.getLeague()).isEqualTo(leagueService.getById(createdLeague.getUuid()));
            assertThat(teamContest.getGamesPlayed()).isEqualTo(0);
            assertThat(teamContest.getGoalsAcquired()).isEqualTo(0);
            assertThat(teamContest.getGoalsLost()).isEqualTo(0);
            assertThat(teamContest.getPoints()).isEqualTo(0);
            assertThat(teamContest.getWins()).isEqualTo(0);
            assertThat(teamContest.getLosses()).isEqualTo(0);
            assertThat(teamContest.getDraws()).isEqualTo(0);
        }
        List<Match> matchesInLeague = leagueService.getAllMatchesInLeague(createdLeague.getUuid());
        Match testMatch = matchesInLeague.getFirst();
        matchService.endMatch(testMatch.getUuid(), MatchScoreDTO.MatchResultDto.builder()
                .team1Score(MatchScoreDTO.TeamScoreDto.builder()
                        .teamId(testMatch.getHomeTeam().getUuid())
                        .goals(10)
                        .lostGoals(5)
                        .build())
                .team2Score(MatchScoreDTO.TeamScoreDto.builder()
                        .teamId(testMatch.getAwayTeam().getUuid())
                        .goals(5)
                        .lostGoals(10)
                        .build()).build());
        List<TeamContest> updatedTeamContests = underTestService.findTeamContestsInCertainLeague(createdLeague.getUuid());
        assertThat(existingTeamContests.size()).isEqualTo(createdLeague.getTeams().size());
        for (TeamContest teamContest : updatedTeamContests) {
            if (teamContest.getTeam().equals(testMatch.getHomeTeam())) {
                assertThat(teamContest.getGamesPlayed()).isEqualTo(1);
                assertThat(teamContest.getGoalsAcquired()).isEqualTo(10);
                assertThat(teamContest.getGoalsLost()).isEqualTo(5);
                assertThat(teamContest.getPoints()).isEqualTo(3);
                assertThat(teamContest.getWins()).isEqualTo(1);
                assertThat(teamContest.getLosses()).isEqualTo(0);
                assertThat(teamContest.getDraws()).isEqualTo(0);
        } else if (teamContest.getTeam().equals(testMatch.getAwayTeam())) {
                assertThat(teamContest.getGamesPlayed()).isEqualTo(1);
                assertThat(teamContest.getGoalsAcquired()).isEqualTo(5);
                assertThat(teamContest.getGoalsLost()).isEqualTo(10);
                assertThat(teamContest.getPoints()).isEqualTo(0);
                assertThat(teamContest.getWins()).isEqualTo(0);
                assertThat(teamContest.getLosses()).isEqualTo(1);
                assertThat(teamContest.getDraws()).isEqualTo(0);
            }
            else {
                assertThat(teamContest.getGamesPlayed()).isEqualTo(0);
                assertThat(teamContest.getGoalsAcquired()).isEqualTo(0);
                assertThat(teamContest.getGoalsLost()).isEqualTo(0);
                assertThat(teamContest.getPoints()).isEqualTo(0);
                assertThat(teamContest.getWins()).isEqualTo(0);
                assertThat(teamContest.getLosses()).isEqualTo(0);
                assertThat(teamContest.getDraws()).isEqualTo(0);
            }
    }
}
}
