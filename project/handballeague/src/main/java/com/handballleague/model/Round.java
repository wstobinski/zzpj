package com.handballleague.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "round")
public class Round {

    @Id
    private Long uuid;

    @OneToMany(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "round_id", referencedColumnName = "uuid")
    @JsonManagedReference
    private List<Match> matches;
    @Column
    @Positive(message = "Round number has to be a positive number")
    private int number;

    @Column
    private LocalDateTime startDate;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "league_uuid", referencedColumnName = "uuid")
    private League contest;

    public Round(int number, LocalDateTime startDate, League contest) {
        this.number = number;
        this.startDate = startDate;
        this.contest = contest;
    }

    @Override
    public String toString() {
        return "Round{" +
                "uuid=" + uuid +
                ", number=" + number +
                ", startDate=" + startDate +
                ", contest=" + contest +
                '}';
    }
}
