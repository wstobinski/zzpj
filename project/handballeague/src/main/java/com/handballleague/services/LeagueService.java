package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.League;
import com.handballleague.model.Match;
import com.handballleague.model.Round;
import com.handballleague.model.Team;
import com.handballleague.repositories.LeagueRepository;
import com.handballleague.repositories.MatchRepository;
import com.handballleague.repositories.RoundRepository;
import com.handballleague.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class LeagueService implements HandBallService<League>{
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final RoundRepository roundRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository, TeamRepository teamRepository, RoundRepository roundRepository, MatchRepository matchRepository) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.roundRepository = roundRepository;
        this.matchRepository = matchRepository;
    }


    @Transactional
    public void generateSchedule(League league) {
        List<Team> teams = league.getTeams();
        for(Team t: teams) {
            System.out.println(t.getTeamName());
        }
        if (teams.size() % 2 != 0) {
            teams.add(null);  // Add dummy team for an odd number of teams
        }
        int numRounds = teams.size() - 1;
        int numMatchesPerRound = teams.size() / 2;

        for (int round = 0; round < numRounds; round++) {
            // Create the Round first
            Round currentRound = new Round();
            currentRound.setUuid(generateRandomLongUUID());
            currentRound.setNumber(round + 1);
            currentRound.setContest(league);
            currentRound.setStartDate(LocalDateTime.now().plusDays(7L * round));

            // Save the Round to generate an ID
            roundRepository.save(currentRound);

            System.out.println("Current round: " + currentRound);

//            System.out.println("All rounds in db: " + roundRepository.findAll().getFirst().getUuid());

            for (int match = 0; match < numMatchesPerRound; match++) {
                int homeIndex = (round + match) % (teams.size() - 1);
                int awayIndex = (teams.size() - 1 - match + round) % (teams.size() - 1);
                if (match == 0) {
                    awayIndex = teams.size() - 1;
                }
                Team home = teams.get(homeIndex);
                Team away = teams.get(awayIndex);
                System.out.println("Home: " + home.getTeamName() + ", " + home.getUuid() + ": Away " + away.getTeamName() + ", " + away.getUuid());
                if (home != null && away != null) {
                    Match newMatch = new Match();
                    newMatch.setUuid(generateRandomLongUUID());
                    newMatch.setGameDate(LocalDateTime.now().plusDays(7L * round));
                    newMatch.setHomeTeam(home);
                    newMatch.setAwayTeam(away);
                    newMatch.setRound(currentRound);  // Associate with the saved Round
                    System.out.println("New Match: " + newMatch);
                    matchRepository.save(newMatch);  // Save the Match
                }
            }
            rotateTeams(teams);  // Rotate teams for the next round
        }
    }


    private void rotateTeams(List<Team> teams) {
        Team temp = teams.removeFirst();
        teams.add(temp);
    }

    public static long generateRandomLongUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Override
    public League create(League league) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(league == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(league)) throw new EntityAlreadyExistsException("League with given data already exists in the database");
        if(league.getName().isEmpty() ||
            league.getStartDate() == null) throw new InvalidArgumentException("At least one of league parameters is invalid.");

        leagueRepository.save(league);

        return league;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException{
        if(id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if(leagueRepository.existsById(id)) {
            leagueRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("League with id: " + id + " not found in the database.");
        }
        return true;
    }

    @Override
    public League update(Long id, League newLeague) throws InvalidArgumentException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (newLeague == null)
            throw new InvalidArgumentException("New league is null.");
        if (newLeague.getName().isEmpty() || newLeague.getStartDate() == null ||
        newLeague.getFinishedDate() == null || newLeague.getLastModifiedDate() == null)
            throw new InvalidArgumentException("Passed invalid arguments.");

        League leagueToChange = leagueRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("League with given id was not found in the database."));

        leagueToChange.setName(newLeague.getName());
        leagueToChange.setStartDate(newLeague.getStartDate());
        leagueToChange.setLastModifiedDate(newLeague.getLastModifiedDate());
        leagueToChange.setFinishedDate(newLeague.getFinishedDate());

        return leagueRepository.save(leagueToChange);
    }

    @Override
    public League getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<League> optionalLeague = leagueRepository.findById(id);
        if (optionalLeague.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Object with given id was not found in the database.");

        return optionalLeague.get();
    }

    @Override
    public List<League> getAll() {
        return leagueRepository.findAll();
    }

    @Override
    public boolean checkIfEntityExistsInDb(League league) {
        Iterable<League> allLeagues = leagueRepository.findAll();

        for(League l : allLeagues) {
            if(league.equals(l)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long leagueID) {
        return leagueRepository.findAll().stream().filter(league -> league.getUuid().equals(leagueID)).toList().size() == 1;

    }

    public League addLeagueToTeam(Long leagueId, Long teamId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("League not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team not found"));

        league.getTeams().add(team);

        leagueRepository.save(league);

        return league;
    }

    public League removeTeamFromLeague(Long leagueId, Long teamId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("League not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Team not found"));

        league.getTeams().remove(team);

        leagueRepository.save(league);

        return league;
    }

}
