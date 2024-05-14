package com.handballleague.services;

import com.handballleague.DTO.GenerateScheduleDTO;
import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.*;
import com.handballleague.repositories.*;
import com.handballleague.util.DateManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.handballleague.util.UUIDGenerator.generateRandomIntegerUUID;

@Service
public class LeagueService implements HandBallService<League>{
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final RoundRepository roundRepository;
    private final MatchRepository matchRepository;
    private final RefereeRepository refereeRepository;
    private final TeamContestService teamContestService;

    @Autowired
    public LeagueService(LeagueRepository leagueRepository, TeamRepository teamRepository, RoundRepository roundRepository, MatchRepository matchRepository, RefereeRepository refereeRepository, @Lazy TeamContestService teamContestService) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.roundRepository = roundRepository;
        this.matchRepository = matchRepository;
        this.refereeRepository = refereeRepository;
        this.teamContestService = teamContestService;
    }


    @Transactional
    public void generateSchedule(League league, GenerateScheduleDTO generateScheduleDTO) throws EntityAlreadyExistsException{
        if(league.isScheduleGenerated()) throw new EntityAlreadyExistsException("This league already has a schedule generated.");
        List<Team> teams = new ArrayList<>(league.getTeams());
        int numTeams = teams.size();

        // If odd number of teams, add a dummy team for bye weeks
        if (numTeams % 2 != 0) {
            teams.add(null);
            numTeams++;
        }
        String[] defaultHourSplit = generateScheduleDTO.getDefaultHour().split(":");
        int defaultHour = Integer.parseInt(defaultHourSplit[0]);
        int defaultMinute = Integer.parseInt(defaultHourSplit[1]);
        LocalDateTime firstRoundStartDate = generateScheduleDTO
                .getStartDate()
                .with(TemporalAdjusters.nextOrSame(generateScheduleDTO.getDefaultDay()));

        for (int round = 0; round < numTeams - 1; round++) {
            LocalDateTime matchDate = firstRoundStartDate
                    .plusWeeks(round)
                    .withHour(defaultHour)
                    .withMinute(defaultMinute)
                    .withSecond(0)
                    .withNano(0);

            while(!DateManager.isDateValid(matchDate))  matchDate = firstRoundStartDate
                    .plusWeeks(1)
                    .withHour(defaultHour)
                    .withMinute(defaultMinute)
                    .withSecond(0)
                    .withNano(0);

            Round currentRound = new Round();
            currentRound.setUuid(generateRandomIntegerUUID());
            currentRound.setNumber(round + 1);
            currentRound.setContest(league);
            currentRound.setStartDate(matchDate);
            roundRepository.save(currentRound);

            for (int match = 0; match < numTeams / 2; match++) {
                int homeIndex = match;
                int awayIndex = (numTeams - 1) - match;

                if (homeIndex == awayIndex || teams.get(homeIndex) == null || teams.get(awayIndex) == null) {
                    continue;  // Skip if dummy team involved or same team
                } else {
                    Team home = teams.get(homeIndex);
                    Team away = teams.get(awayIndex);

                    Match newMatch = new Match();
                    newMatch.setUuid(generateRandomIntegerUUID());
                    newMatch.setGameDate(matchDate);
                    newMatch.setHomeTeam(home);
                    newMatch.setAwayTeam(away);
                    newMatch.setRound(currentRound);
                    newMatch.setReferee(drawReferee(match));
                    matchRepository.save(newMatch);
                }
            }

            rotateTeams(teams);
        }
        league.setScheduleGenerated(true);
    }

    private static void rotateTeams(List<Team> teams) {
        if (teams.size() < 2) return;

        Team temp = teams.get(1);

        for (int i = 1; i < teams.size() - 1; i++) {
            teams.set(i, teams.get(i + 1));
        }
        teams.set(teams.size() - 1, temp);
    }

    private Referee drawReferee(int matchNumber) {
        List<Referee> referees = refereeRepository.findAll();
        return referees.get(matchNumber % referees.size());
    }

    @Override
    @Transactional
    public League create(League league) throws InvalidArgumentException, EntityAlreadyExistsException {
        if(league == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if(checkIfEntityExistsInDb(league)) throw new EntityAlreadyExistsException("League with given data already exists in the database");
        if(league.getName().isEmpty() ||
            league.getStartDate() == null) throw new InvalidArgumentException("At least one of league parameters is invalid.");
        if(league.getTeams().size() > 12 || league.getTeams().size() < 3)
            throw new InvalidArgumentException("League needs to have at least 3 teams, and no more than 12 teams.");

        League createdLeague = leagueRepository.save(league);
        for (Team team: createdLeague.getTeams()) {
            teamContestService.create(createdLeague.getUuid(), team.getUuid());
        }
        return createdLeague;
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
        if (newLeague.getName().isEmpty() || newLeague.getStartDate() == null)
            throw new InvalidArgumentException("Passed invalid arguments.");

        League leagueToChange = leagueRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("League with given id was not found in the database."));

        leagueToChange.setName(newLeague.getName());
        leagueToChange.setStartDate(newLeague.getStartDate());
        leagueToChange.setLastModifiedDate(newLeague.getLastModifiedDate());
        leagueToChange.setFinishedDate(newLeague.getFinishedDate());
        leagueToChange.setTeams(newLeague.getTeams());

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

        teamContestService.create(leagueId, teamId);

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

    public List<Match> getAllMatchesInLeague(Long leagueId) {
        List<Match> leagueMatches = new ArrayList<>();

        for(Match m : matchRepository.findAll())
            if(m.getRound().getContest().equals(getById(leagueId)))
                leagueMatches.add(m);

        return leagueMatches;
    }

    public LocalDateTime finishLeague(Long leagueId) throws RuntimeException {
        try {
            League league = leagueRepository.findById(leagueId)
                    .orElseThrow(() -> new ObjectNotFoundInDataBaseException("League not found"));

            if(league.getFinishedDate() != null)
                throw new RuntimeException("League is already finished");

            LocalDateTime finishedTime = LocalDateTime.now();
            league.setFinishedDate(finishedTime);
            leagueRepository.save(league);

            return finishedTime;
        } catch (Exception e) {
            throw new RuntimeException("Error finishing the league: " + e.getMessage(), e);
        }
    }


}
