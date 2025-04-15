package org.example.icatch.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.icatch.enums.AiStatus;
import org.example.icatch.enums.DeviceStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "device")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long device_id;

    @ManyToOne
    @JoinColumn(name = "users", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String device_ip;

    @Enumerated(EnumType.STRING)
    private AiStatus ai_Status;

    @Enumerated(EnumType.STRING)
    private DeviceStatus device_status;

    private Double temperature;

    private Double humidity;

    private Double version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
