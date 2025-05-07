package org.example.icatch.Target;


import org.example.icatch.Camera.CameraResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class TargetResponseDTO {
    private Integer targetId;
    private Integer userId;
    private String targetType;
    private List<CameraResponseDTO> cameras = new ArrayList<>();

    // 생성자
    public TargetResponseDTO(Target target) {
        this.targetId = target.getTargetId();
        this.userId = target.getUserId();
        this.targetType = target.getTargetType().toString();
    }

    // Getters
    public Integer getTargetId() {
        return targetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getTargetType() {
        return targetType;
    }

    public List<CameraResponseDTO> getCameras() {
        return cameras;
    }

    public void setCameras(List<CameraResponseDTO> cameras) {
        this.cameras = cameras;
    }
}