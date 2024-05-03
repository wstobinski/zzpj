package com.handballleague.services;

import com.handballleague.DTO.MatchScoreDTO;
import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Match;
import com.handballleague.model.Score;
import com.handballleague.model.Team;
import com.handballleague.repositories.MatchRepository;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.ScoreRepository;
import com.handballleague.repositories.TeamRepository;
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

    @Autowired
    public MatchService(MatchRepository matchRepository, ScoreRepository scoreRepository, TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.scoreRepository = scoreRepository;
        this.teamRepository = teamRepository;
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

}