package com.handballleague.model;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Player {
    private final String uuid;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int pitchNumber;
    private boolean isCaptain;
    private POSITIONS position;
    private boolean isSuspended;

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
                Objects.equals(phoneNumber, player.phoneNumber) &&
                position == player.position;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, firstName, lastName, phoneNumber, pitchNumber, isCaptain, position, isSuspended);
    }

}
