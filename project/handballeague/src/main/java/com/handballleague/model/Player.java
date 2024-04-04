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
@Table(name = "Player")
public class Player {
    @Id
    @SequenceGenerator(
            name = "player_sequence",
            sequenceName = "player_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    private Long uuid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int pitchNumber;
    private boolean isCaptain;
    private boolean isSuspended;

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
