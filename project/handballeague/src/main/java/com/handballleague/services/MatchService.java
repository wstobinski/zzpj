package com.handballleague.services;

import com.handballleague.DTO.MatchScoreDTO;
import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.*;
import com.handballleague.repositories.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.handballleague.util.UUIDGenerator.generateRandomLongUUID;

@Service
public class MatchService implements HandBallService<Match>{
    private final MatchRepository matchRepository;
    private final ScoreRepository scoreRepository;
    private final TeamRepository teamRepository;
    private final TeamContestRepository teamContestRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository, ScoreRepository scoreRepository, TeamRepository teamRepository, TeamContestRepository teamContestRepository) {
        this.matchRepository = matchRepository;
        this.scoreRepository = scoreRepository;
        this.teamRepository = teamRepository;
        this.teamContestRepository = teamContestRepository;
    }
    @Override
    public Match create(Match entity) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(entity)) throw new EntityAlreadyExistsException("Match with given data already exists in the database");
        if(entity.getHomeTeam() == null ||
                entity.getAwayTeam()  == null ||
                entity.getGameDate() == null) throw new InvalidArgumentException("At least one of match parameters is invalid.");

        matchRepository.save(entity);

        return entity;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(matchRepository.existsById(id)) {
            matchRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("Match with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public Match update(Long id, Match entity) throws InvalidArgumentException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (entity == null)
            throw new InvalidArgumentException("New match is null.");
        if (entity.getHomeTeam() == null ||
                entity.getAwayTeam()  == null ||
                entity.getGameDate() == null) throw new InvalidArgumentException("Passed invalid arguments.");

        Match matchToChange = matchRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Match with given id was not found in the database."));

        matchToChange.setGameDate(entity.getGameDate());
        matchToChange.setHomeTeam(entity.getHomeTeam());
        matchToChange.setAwayTeam(entity.getAwayTeam());

        return matchRepository.save(matchToChange);
    }

    @Override
    public Match getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Match> optionalMatch = matchRepository.findById(id);
        if (optionalMatch.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Object with given id was not found in the database.");

        return optionalMatch.get();
    }

    @Override
    public List<Match> getAll() {
        return matchRepository.findAll();
    }

    @Override
    public boolean checkIfEntityExistsInDb(Match entity) {
        Iterable<Match> allMatches = matchRepository.findAll();

        for(Match m : allMatches) {
            if(entity.equals(m)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return matchRepository.findAll().stream().filter(match -> match.getUuid().equals(entityID)).toList().size() == 1;
    }

    public void endMatch(Long matchId, MatchScoreDTO.MatchResultDto matchResult) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        createScore(match, matchResult.getTeam1Score());
        createScore(match, matchResult.getTeam2Score());

        updateTeamContestStats(match, matchResult.getTeam1Score(), matchResult.getTeam2Score());

        match.setFinished(true);
        matchRepository.save(match);
    }

    private void createScore(Match match, MatchScoreDTO.TeamScoreDto teamScore) {
        Team team = teamRepository.findById(teamScore.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Score score = new Score(generateRandomLongUUID(),match,team,teamScore.getGoals(), teamScore.getLostGoals(), teamScore.getFouls(),
                teamScore.getBallPossession(), teamScore.getYellowCards(),teamScore.getRedCards(),teamScore.getTimePenalties());

        System.out.println("Score: " + score);

        scoreRepository.save(score);
    }

    //TODO: Przy tworzeniu zespołów powinno automatycznie generowac sie TeamContest

    public void updateTeamContestStats(Match match, MatchScoreDTO.TeamScoreDto team1Score, MatchScoreDTO.TeamScoreDto team2Score) {
        Contest contest = match.getRound().getContest();  // Assuming Round has a reference to Contest

        // Retrieve Team_Contest records
        TeamContest team1Contest = teamContestRepository.findByTeamAndLeague(match.getHomeTeam(),
                                                                                match.getRound().getContest());
        TeamContest team2Contest = teamContestRepository.findByTeamAndLeague(match.getAwayTeam(),
                                                                                match.getRound().getContest());

        // Update team1 stats
        team1Contest.setGamesPlayed(team1Contest.getGamesPlayed() + 1);
        team1Contest.setGoalsAcquired(team1Contest.getGoalsAcquired() + team1Score.getGoals());
        team1Contest.setGoalsLost(team1Contest.getGoalsLost() + team1Score.getLostGoals());
        team1Contest.setPoints(team1Contest.getPoints() + calculatePoints(team1Score, team2Score));

        // Update team2 stats
        team2Contest.setGamesPlayed(team2Contest.getGamesPlayed() + 1);
        team2Contest.setGoalsAcquired(team2Contest.getGoalsAcquired() + team2Score.getGoals());
        team2Contest.setGoalsLost(team2Contest.getGoalsLost() + team2Score.getLostGoals());
        team2Contest.setPoints(team2Contest.getPoints() + calculatePoints(team2Score, team1Score));

        if(team1Score.getGoals() > team2Score.getGoals()) {
            team1Contest.setWins(team1Contest.getWins() + 1);
            team2Contest.setLosses(team2Contest.getLosses() + 1);
        }
        else if (team1Score.getGoals() == team2Score.getGoals()) {
            team1Contest.setDraws(team1Contest.getDraws() + 1);
            team2Contest.setDraws(team2Contest.getDraws() + 1);
        } else {
            team1Contest.setLosses(team1Contest.getLosses() + 1);
            team2Contest.setWins(team2Contest.getWins() + 1);
        }

        // Save updated records
        teamContestRepository.save(team1Contest);
        teamContestRepository.save(team2Contest);
    }

    private int calculatePoints(MatchScoreDTO.TeamScoreDto teamScore, MatchScoreDTO.TeamScoreDto opponentScore) {
        if (teamScore.getGoals() > opponentScore.getGoals()) {
            return 3;
        } else if (teamScore.getGoals() == opponentScore.getGoals()) {
            return 1;
        } else {
            return 0;
        }
    }

}