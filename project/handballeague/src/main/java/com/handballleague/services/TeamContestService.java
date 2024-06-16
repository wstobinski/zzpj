package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Team;
import com.handballleague.model.TeamContest;
import com.handballleague.repositories.TeamContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamContestService implements HandBallService<TeamContest>{

    private final LeagueService leagueService;
    private final TeamService teamService;
    private final TeamContestRepository teamContestRepository;

    @Autowired
    public TeamContestService(LeagueService leagueService, TeamService teamService, TeamContestRepository teamContestRepository) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.teamContestRepository = teamContestRepository;
    }

    @Override
    public TeamContest create(TeamContest teamContest) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(teamContest == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(teamContest)) throw new EntityAlreadyExistsException("TeamContest with given data already exists in the database");
        if(teamContest.getTeam() == null ||
                teamContest.getLeague() == null) throw new InvalidArgumentException("Team contest needs to be connected with both Team and Contest entities");

        boolean teamExists = teamService.checkIfEntityExistsInDb(teamContest.getTeam());
        if (!teamExists) {
            throw new InvalidArgumentException("Provided Team does not exist in the database");
        }
        boolean leagueExists = leagueService.checkIfEntityExistsInDb(teamContest.getLeague());
        if (!leagueExists) {
            throw new InvalidArgumentException("Provided League does not exist in the database");
        }
        teamContestRepository.save(teamContest);
        return teamContest;
    }

    public TeamContest create(Long leagueID, Long teamID) throws InvalidArgumentException, EntityAlreadyExistsException {
        Team team = teamService.getById(teamID);
        if (team == null) {
            throw new InvalidArgumentException("Provided Team does not exist in the database");
        }
        League league = leagueService.getById(leagueID);
        if (league == null) {
            throw new InvalidArgumentException("Provided League does not exist in the database");
        }
        boolean teamContestExists = this.checkIfEntityExistsInDb(teamID, leagueID);
        if (teamContestExists) {
            throw new EntityAlreadyExistsException("TeamContest for provided league and team already exists");
        }
        TeamContest teamContest = new TeamContest(team, league, 0, 0 , 0, 0, 0, 0);
        teamContestRepository.save(teamContest);

        return teamContest;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(teamContestRepository.existsById(id)) {
            teamContestRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("TeamContest with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public TeamContest update(Long id, TeamContest newTeamContest) {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (newTeamContest == null)
            throw new InvalidArgumentException("New teamContest is null.");
        if (newTeamContest.getTeam() == null || newTeamContest.getLeague() == null)
            throw new InvalidArgumentException("Passed invalid arguments.");

        TeamContest teamContestToUpdate = teamContestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("TeamContest with given id was not found in the database."));

        teamContestToUpdate.updateTo(newTeamContest);
        return teamContestRepository.save(teamContestToUpdate);
    }

    @Override
    public TeamContest getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        return teamContestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("TeamContest with given id was not found in the database."));
    }

    @Override
    public List<TeamContest> getAll() {
        return teamContestRepository.findAllByOrderByPointsDescGoalsAcquiredDescGoalsLostAsc();
    }

    @Override
    public boolean checkIfEntityExistsInDb(TeamContest teamContestToFind) {
        return teamContestRepository.findAll().stream().filter(teamContest -> teamContest.equals(teamContestToFind)).toList().size() == 1;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        throw new RuntimeException("Not applicable");
    }


    public boolean checkIfEntityExistsInDb(Long teamID, Long leagueID) {
        return !teamContestRepository.findAll().stream().filter(teamContest -> teamContest.getTeam().getUuid().equals(teamID)
                && teamContest.getLeague().getUuid().equals(leagueID)).toList().isEmpty();

    }

    public List<TeamContest> findTeamContestsInCertainLeague(Long leagueId) {
      return teamContestRepository
              .findAllByOrderByPointsDescGoalsAcquiredDescGoalsLostAsc()
              .stream().filter(teamContest -> teamContest.getLeague().getUuid().equals(leagueId)).toList();
    }

}
