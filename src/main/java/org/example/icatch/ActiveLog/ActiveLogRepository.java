package org.example.icatch.ActiveLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveLogRepository extends JpaRepository<ActiveLog, Integer> {
    // 수정
    @Query("SELECT COUNT(l) FROM ActiveLog l JOIN l.camera c WHERE c.user.userId = :userId")
    Integer countLogsByUserId(@Param("userId") Integer userId);
}