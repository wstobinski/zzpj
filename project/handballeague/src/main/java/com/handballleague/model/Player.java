package com.handballleague.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(
        name = "player"
)
public class Player {
    @Id
    @SequenceGenerator(
            name = "player_sequence",
            sequenceName = "player_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "player_sequence"
    )
    private Long uuid;
    @Column(
            name = "first_name",
            nullable = false
    )
    @Size(min = 2, max = 50, message = "First name needs to be between [2,50] characters")
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false
    )
    @Size(min = 2, max = 50, message = "Last name needs to be between [2,50] characters")
    private String lastName;
    @Column(
            name = "phone_number"
    )
    private String phoneNumber;

    @Column(
            name = "email"
    )
    private String email;
    @Column(
            name = "pitch_number",
            nullable = false
    )
    @Positive(message = "Pitch number needs to be a positive number")
    private int pitchNumber;
    @Column(
            name = "is_captain",
            nullable = false,
            columnDefinition = "boolean"
    )
    private boolean isCaptain = false;
    @Column(
            name = "is_suspended",
            nullable = false,
            columnDefinition = "boolean"
    )
    private boolean isSuspended = false;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference
    private Team team;

    public Player(String firstName, String lastName, String phoneNumber, int pitchNumber, boolean isCaptain, boolean isSuspended) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.pitchNumber = pitchNumber;
        this.isCaptain = isCaptain;
        this.isSuspended = isSuspended;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return pitchNumber == player.pitchNumber &&
                Objects.equals(firstName, player.firstName) &&
                Objects.equals(lastName, player.lastName) &&
                Objects.equals(phoneNumber, player.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, firstName, lastName, phoneNumber, pitchNumber, isCaptain, isSuspended);
    }

}
