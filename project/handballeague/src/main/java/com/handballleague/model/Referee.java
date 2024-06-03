package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
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

}
