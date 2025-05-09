package org.example.icatch.Target;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetRepository extends JpaRepository<Target, Integer> {

    List<Target> findByUserId(Integer userId);
    long countByUserId(Integer userId);
}