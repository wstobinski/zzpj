package com.handballleague.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "league")
public class League extends Contest{
    @OneToMany
    @JoinColumn(name = "league_id", referencedColumnName = "uuid")
    @JsonManagedReference
    private List<Team> teams;

    @OneToMany
    @JsonManagedReference
    @JoinColumn(name = "league_uuid", referencedColumnName = "uuid")
    private List<Round> rounds;

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
