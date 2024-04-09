package com.handballleague.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

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
}
