package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Match;
import com.handballleague.model.Score;
import com.handballleague.model.Team;
import com.handballleague.repositories.ScoreRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScoreService implements HandBallService<Score> {
    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    public Score create(@Valid Score entity) throws InvalidArgumentException, EntityAlreadyExistsException {
        if (entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if (checkIfEntityExistsInDb(entity))
            throw new EntityAlreadyExistsException("Score with given data already exists in the database");
        if (entity.getMatch() == null ||
                entity.getTeam() == null)
            throw new InvalidArgumentException("At least one of Score parameters is invalid.");

        scoreRepository.save(entity);

        return entity;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if (scoreRepository.existsById(id)) {
            scoreRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("Score with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public Score update(Long id, @Valid Score entity) {
        if (entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if (checkIfEntityExistsInDb(entity))
            throw new EntityAlreadyExistsException("Score with given data already exists in the database");
        if (entity.getMatch() == null ||
                entity.getTeam() == null)
            throw new InvalidArgumentException("At least one of Score parameters is invalid.");

        Score scoreToChange = scoreRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Score with given id was not found in the database."));

        scoreToChange.setMatch(entity.getMatch());
        scoreToChange.setTeam(entity.getTeam());
        scoreToChange.setGoals(entity.getGoals());
        scoreToChange.setLostGoals(entity.getLostGoals());
        scoreToChange.setFouls(entity.getFouls());
        scoreToChange.setBallPossession(entity.getBallPossession());
        scoreToChange.setYellowCards(entity.getYellowCards());
        scoreToChange.setRedCards(entity.getRedCards());
        scoreToChange.setTimePenalties(entity.getTimePenalties());

        return scoreRepository.save(scoreToChange);
    }

    @Override
    public Score getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Score> optionalScore = scoreRepository.findById(id);
        if (optionalScore.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Score with given id was not found in the database.");

        return optionalScore.get();
    }

    @Override
    public List<Score> getAll() {
        return scoreRepository.findAll();
    }

    @Override
    public boolean checkIfEntityExistsInDb(@Valid Score entity) {
        Iterable<Score> allScores = scoreRepository.findAll();

        for (Score s : allScores) {
            if (entity.equals(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return scoreRepository.findAll().stream().filter(score -> score.getUuid().equals(entityID)).toList().size() == 1;
    }


    private Map<Long, Integer> getMatchesAndWinners(Long team1Id, Long team2Id) {
        // TODO some exception handling

        List<Score> scores = scoreRepository.findScoresByTeams(team1Id, team2Id);

        Map<Long, Integer> matchResults = new HashMap<>(Map.of(team1Id, 0, team2Id, 0));

        for (Score score : scores) {
            if (score.getGoals() > score.getLostGoals()) {
                matchResults.put(team1Id, matchResults.get(team1Id) + 1);
            } else if (score.getGoals() < score.getLostGoals()) {
                matchResults.put(team2Id, matchResults.get(team2Id) + 1);
            }
        }


        return matchResults;
    }

    public Map<String, Double> getWinningChances(Team team1, Team team2) {

        Map<Long, Integer> matchResults = getMatchesAndWinners(team1.getUuid(), team2.getUuid());
        int summed = matchResults.values().stream().mapToInt(Integer::intValue).sum();
        if (summed == 0) {
            return Map.of(team1.getTeamName(), 0.5, team2.getTeamName(), 0.5);
        }
        return Map.of(team1.getTeamName(), (double) matchResults.get(team1.getUuid()) / summed, team2.getTeamName(), (double) matchResults.get(team2.getUuid()) / summed);
    }
}