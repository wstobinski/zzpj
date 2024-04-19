package com.handballleague.servicesTests;

import com.handballleague.model.League;
import com.handballleague.model.Player;
import com.handballleague.model.Team;
import com.handballleague.model.TeamContest;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.TeamContestRepository;
import com.handballleague.services.LeagueService;
import com.handballleague.services.PlayerService;
import com.handballleague.services.TeamContestService;
import com.handballleague.services.TeamService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static java.time.Duration.ofSeconds;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TeamContestServiceTest {

    @Mock
    private TeamContestRepository teamContestRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private LeagueService leagueService;

    private AutoCloseable autoCloseable;
    private TeamContestService underTestService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTestService = new TeamContestService(leagueService, teamService, teamContestRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

//    @Test
//    void createTeamContest_Test() {
//        //given
//        Team team = new Team("Test team");
//        League league = new League("Test league", LocalDateTime.now());
//
//        // Save Team and League entities to the database
//        teamService.create(team);
//        leagueService.create(league);
//
//        Awaitility.await().atMost(ofSeconds(5)).until(() -> teamService.checkIfEntityExistsInDb(team.getUuid()) && leagueService.checkIfEntityExistsInDb(league.getUuid()));
//
//        TeamContest teamContest = new TeamContest(team, league, 0, 0, 0, 0, 0, 0);
//
//
//
//        //when
//        underTestService.create(teamContest);
//
//        //then
//        ArgumentCaptor<TeamContest> argumentCaptor = ArgumentCaptor.forClass(TeamContest.class);
//
//        verify(teamContestRepository).save(argumentCaptor.capture());
//
//        assertThat(argumentCaptor.getValue()).isEqualTo(teamContest);
//    }
}
