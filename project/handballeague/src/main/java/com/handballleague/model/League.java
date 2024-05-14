package com.handballleague.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @OneToMany(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "league_id", referencedColumnName = "uuid")
    @JsonManagedReference
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
