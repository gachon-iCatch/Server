package org.example.icatch.Camera;


// 카메라 정보를 담는 DTO 클래스
public class CameraResponseDTO {
    private Integer cameraId;
    private String cameraName;
    private String isEnabled;  // Boolean에서 String으로 변경

    // 기본 생성자
    public CameraResponseDTO() {
    }

    // Camera 엔티티를 받아 변환하는 생성자
    public CameraResponseDTO(Camera camera) {
        this.cameraId = camera.getCameraId();
        this.cameraName = camera.getCameraName();
        this.isEnabled = camera.getIsEnabled();
    }

    // Getters and Setters
    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getIsEnabled() {  // Boolean에서 String으로 반환 타입 변경
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {  // Boolean에서 String으로 매개변수 타입 변경
        this.isEnabled = isEnabled;
    }
}