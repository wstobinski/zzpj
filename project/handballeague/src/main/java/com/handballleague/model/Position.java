package com.handballleague.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "position")
public class Position {
    @Id
    @SequenceGenerator(
            name = "position_sequence",
            sequenceName = "position_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "position_sequence"
    )
    private Long position_id;

    @Column(
            name = "position_name",
            nullable = false
    )
    private POSITIONS name;

    public Position(POSITIONS name) {
        this.name = name;
    }
}
