package com.handballleague.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Setter
@Getter
public class GenerateScheduleDTO {
    private LocalDateTime startDate;
    private String defaultHour;
    private DayOfWeek defaultDay;

}
