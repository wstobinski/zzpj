package com.handballleague.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
@Builder
public class GenerateScheduleDTO {
    private LocalDateTime startDate;
    private String defaultHour;
    private DayOfWeek defaultDay;
}
