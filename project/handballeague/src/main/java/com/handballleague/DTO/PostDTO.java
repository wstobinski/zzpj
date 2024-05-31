package com.handballleague.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
    private String title;
    private String content;
    private LocalDateTime postedDate;
}
