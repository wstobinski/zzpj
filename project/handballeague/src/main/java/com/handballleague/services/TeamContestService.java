package com.handballleague.services;

import com.handballleague.model.TeamContest;

import java.util.List;

public class TeamContestService implements HandBallService<TeamContest>{
    // TODO: Implementacja całego serwisu

    @Override
    public TeamContest create(TeamContest entity) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public TeamContest update(Long id, TeamContest entity) {
        return null;
    }

    @Override
    public TeamContest getById(Long id) {
        return null;
    }

    @Override
    public List<TeamContest> getAll() {
        return null;
    }

    @Override
    public boolean checkIfEntityExistsInDb(TeamContest entity) {
        return false;
    }

    public List<TeamContest> findTeamContestsInCertainLeague(Long leagueId) {
        return null;
    }

    public TeamContest findTeamContestForTeam(Long id) {
        return null;
    }
}
