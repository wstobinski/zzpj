package com.handballleague.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = true)
    private LocalDateTime lastModifiedDate;
    @Column(nullable = true)
    private LocalDateTime finishedDate;
    @Column(nullable = false)
    private boolean isScheduleGenerated = false;

    public Contest(String name, LocalDateTime startDate) {
        this.name = name;
        this.startDate = startDate;
        this.lastModifiedDate = LocalDateTime.now();
    }

    public abstract void addTeam(Team team);
    public abstract void generateSchedule();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contest contest)) return false;
        return Objects.equals(getName(), contest.getName()) && Objects.equals(getStartDate(), contest.getStartDate()) && Objects.equals(getFinishedDate(), contest.getFinishedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getStartDate(), getFinishedDate());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Contest.class.getSimpleName() + "[", "]")
                .add("uuid=" + uuid)
                .add("name='" + name + "'")
                .add("startDate=" + startDate)
                .add("lastModifiedDate=" + lastModifiedDate)
                .add("finishedDate=" + finishedDate)
                .add("isScheduleGenerated=" + isScheduleGenerated)
                .toString();
    }
}
