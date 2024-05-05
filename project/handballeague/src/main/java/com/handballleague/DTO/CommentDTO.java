package com.handballleague.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    private String content;
    private Long matchId;
    private Long refereeId;
    private Long authorId;
}

