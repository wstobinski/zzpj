package com.handballleague.repositories;

import com.handballleague.model.Referee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefereeRepository extends JpaRepository<Referee, Long> {
}
