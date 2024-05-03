package com.handballleague.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team")
public class Team {
    @Id
    @SequenceGenerator(
            name = "team_sequence",
            sequenceName = "team_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "team_sequence"
    )
    private Long uuid;

    @Column(
            name = "team_name",
            nullable = false,
            unique = true
    )
    @Size(min = 3, max = 50, message = "Team name needs to be between [3,50] characters")
    private String teamName;

    @OneToMany(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "team_id", referencedColumnName = "uuid")
    @JsonManagedReference
    private List<Player> players;

    public Team(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(teamName, team.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, teamName, players);
    }

}
