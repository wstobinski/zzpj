package com.handballleague.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "league")
public class League extends Contest{
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    private List<Team> teams;

    public League(String name, LocalDateTime startDate) {
        super(name, startDate);
    }

    @Override
    public void addTeam(Team team) {

    }

    @Override
    public void generateSchedule() {

    }

}
