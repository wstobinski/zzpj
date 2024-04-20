package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Round;
import com.handballleague.repositories.LeagueRepository;
import com.handballleague.repositories.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoundService implements HandBallService<Round> {

    private final RoundRepository roundRepository;
    private final LeagueRepository leagueRepository;

    @Autowired
    public RoundService(RoundRepository roundRepository, LeagueRepository leagueRepository) {
        this.roundRepository = roundRepository;
        this.leagueRepository = leagueRepository;
    }


    @Override
    public Round create(Round entity) {
        if (entity == null) {
            throw new InvalidArgumentException("Passed round is null");
        }
        if(checkIfEntityExistsInDb(entity)) {
            throw new EntityAlreadyExistsException("Round with given data already exists in the database");
        }
        roundRepository.save(entity);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(roundRepository.existsById(Long.toString(id))) {
            roundRepository.deleteById(Long.toString(id));
        } else {
            throw new ObjectNotFoundInDataBaseException("Round with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public Round update(Long id, Round entity) {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (entity == null)
            throw new InvalidArgumentException("New round is null.");

        Round roundToChange = roundRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Round with given id was not found in the database."));

        roundToChange.setContest(entity.getContest());
        roundToChange.setNumber(entity.getNumber());
        roundToChange.setStartDate(entity.getStartDate());

        return roundRepository.save(roundToChange);
    }

    @Override
    public Round getById(Long id) {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Round> optionalRound = roundRepository.findById(String.valueOf(id));
        if (optionalRound.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Object with given id was not found in the database.");

        return optionalRound.get();
    }

    public List<Round> getByLeagueId(Long leagueId) {
        if (leagueId <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<League> league = leagueRepository.findById(leagueId);
        List<Round> rounds = roundRepository.findByContest
                (league.orElseThrow(() -> new ObjectNotFoundInDataBaseException("League not found in the database")))
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Contest with given id was not found in the database."));
        if (rounds.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Rounds for given contest id were not found in the database.");

        return rounds;
    }

    @Override
    public List<Round> getAll() {
        return roundRepository.findAll();
    }

    @Override
    public boolean checkIfEntityExistsInDb(Round entity) {
        return roundRepository.findAll().stream().filter(round -> round.equals(entity)).toList().size() != 0;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return roundRepository.findAll().stream().filter(round -> round.getUuid().equals(entityID)).toList().size() != 0;
    }
}
