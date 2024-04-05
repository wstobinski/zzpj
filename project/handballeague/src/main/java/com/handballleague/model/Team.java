package com.handballleague.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
            nullable = false
    )
    private String teamName;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "player_id", referencedColumnName = "uuid")
    private List<Player> players;
}
