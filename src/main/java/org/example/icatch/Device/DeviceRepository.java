package org.example.icatch.Device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findFirstByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    //추가
    Device findByDeviceIp(String deviceIp);
}