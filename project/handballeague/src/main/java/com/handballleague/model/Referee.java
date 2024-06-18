package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "referee")
public class Referee {
    @Id
    @SequenceGenerator(
            name = "referee_sequence",
            sequenceName = "referee_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "referee_sequence"
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
            name = "email",
            nullable = false

    )
    private String email;
    @Column(
            name = "rating"
    )
    private double rating;

    public Referee(String firstName, String lastName, String phoneNumber, String email, double rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.rating = rating;
    }
}
