package org.example.icatch.repository;

import org.example.icatch.model.Device;
import org.example.icatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    List<Device> findByUser_UserId(Integer userId);
    Integer countByUserId(Integer userId);
}