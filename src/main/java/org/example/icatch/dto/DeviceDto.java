package org.example.icatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class DeviceDto {
    private Integer deviceId;
    private String aiStatus;
    private String deviceStatus;
    private Double temperature;
    private Double humidity;
    private Integer version;
}