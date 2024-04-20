package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "round")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @Column
    @Positive(message = "Round number has to be a positive number")
    private int number;

    @Column
    private LocalDateTime startDate;

    @ManyToOne
    @JoinColumn(name = "league_uuid", referencedColumnName = "uuid")
    private League contest;

    public Round(int number, LocalDateTime startDate, League contest) {
        this.number = number;
        this.startDate = startDate;
        this.contest = contest;
    }
}
