package com.handballleague.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(
        name = "player",
        uniqueConstraints = {
                @UniqueConstraint(name = "player_phone_number_unique",columnNames = "phone_number")
        }
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
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false
    )
    private String lastName;
    @Column(
            name = "phone_number",
            nullable = false
    )
    private String phoneNumber;
    @Column(
            name = "pitch_number",
            nullable = false
    )
    private int pitchNumber;
//    @ManyToOne
//    @JoinColumn(name = "position_id", nullable = false)
//    private Position position;
    @Column(
            name = "is_captain",
            nullable = false,
            columnDefinition = "boolean"
    )
    private boolean isCaptain;
    @Column(
            name = "is_suspended",
            nullable = false,
            columnDefinition = "boolean"
    )
    private boolean isSuspended;
//    @ManyToOne
//    @JoinColumn(name = "team_id")
//    private Team team;

    public Player(String firstName, String lastName, String phoneNumber, int pitchNumber, boolean isCaptain, boolean isSuspended) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.pitchNumber = pitchNumber;
//        this.position = position;
        this.isCaptain = isCaptain;
        this.isSuspended = isSuspended;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return pitchNumber == player.pitchNumber &&
                isCaptain == player.isCaptain &&
                isSuspended == player.isSuspended &&
                Objects.equals(uuid, player.uuid) &&
                Objects.equals(firstName, player.firstName) &&
                Objects.equals(lastName, player.lastName) &&
                Objects.equals(phoneNumber, player.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, firstName, lastName, phoneNumber, pitchNumber, isCaptain, isSuspended);
    }

}
