package org.example.icatch.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {
    Optional<Setting> findByUserId(Integer userId);
}