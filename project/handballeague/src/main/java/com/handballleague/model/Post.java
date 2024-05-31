package com.handballleague.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @SequenceGenerator(
            name = "post_sequence",
            sequenceName = "post_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "post_sequence"
    )
    private Long uuid;

    @Column(
            name = "title",
            nullable = false
    )
    @Size(min = 2, max = 128, message = "Title needs to have [2,128] characters")
    private String title;

    @Column(
            name = "content",
            nullable = false
    )
    @Size(min = 2, max = 512, message = "Message needs to have [2,512] characters")
    private String content;

    @Column(
            name = "posted_date"
    )
    private LocalDateTime postedDate;


}
