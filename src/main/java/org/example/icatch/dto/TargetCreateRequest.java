package org.example.icatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetCreateRequest {
    private Integer user_Id;
    private Integer device_Id;
    private String camera_name;
    private String target_Type;
}