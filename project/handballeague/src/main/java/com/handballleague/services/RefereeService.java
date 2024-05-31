package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Referee;
import com.handballleague.repositories.MatchRepository;
import com.handballleague.repositories.RefereeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RefereeService implements HandBallService<Referee>{
    private final RefereeRepository refereeRepository;
    private final MatchRepository matchRepository;

    public RefereeService(RefereeRepository refereeRepository, MatchRepository matchRepository) {
        this.refereeRepository = refereeRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public Referee create(Referee entity) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(entity)) throw new EntityAlreadyExistsException("Referee with given data already exists in the database");
        if(entity.getFirstName().isEmpty() ||
                entity.getLastName().isEmpty() ||
                entity.getEmail().isEmpty()) throw new InvalidArgumentException("At least one of referee parameters is invalid.");
        refereeRepository.save(entity);

        return entity;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(refereeRepository.existsById(id)) {
            refereeRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("Referee with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public Referee update(Long id, Referee entity) throws InvalidArgumentException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (entity == null)
            throw new InvalidArgumentException("New refere is null.");
        if(entity.getFirstName().isEmpty() ||
                entity.getLastName().isEmpty() ||
                entity.getPhoneNumber().isEmpty()) throw new InvalidArgumentException("At least one of referees parameters is invalid.");

        Referee refereeToChange = refereeRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Referee with given id was not found in the database."));

        refereeToChange.setFirstName(entity.getFirstName());
        refereeToChange.setLastName(entity.getLastName());
        refereeToChange.setPhoneNumber(entity.getPhoneNumber());
        refereeToChange.setEmail(entity.getEmail());

        return refereeRepository.save(refereeToChange);
    }

    @Override
    public Referee getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Referee> optionalReferee = refereeRepository.findById(id);
        if (optionalReferee.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Referee with given id was not found in the database.");

        return optionalReferee.get();
    }

    @Override
    public List<Referee> getAll() {
        Sort sortByRefereeId = Sort.by(Sort.Direction.ASC, "uuid");
        return refereeRepository.findAll(sortByRefereeId);
    }

    @Override
    public boolean checkIfEntityExistsInDb(Referee entity) {
        Iterable<Referee> allReferees = refereeRepository.findAll();

        for(Referee r : allReferees) {
            if(entity.equals(r)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return refereeRepository.findAll().stream().filter(referee -> referee.getUuid().equals(entityID)).toList().size() == 1;
    }
}
