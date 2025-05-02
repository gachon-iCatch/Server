package org.example.icatch.Device;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;



public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findFirstByUser_UserIdOrderByCreatedAtDesc(Integer userId);
}