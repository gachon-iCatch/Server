package org.example.icatch.Gesture;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gesture")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Gesture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gesture_id")
    private Integer gestureId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "camera_id", nullable = false)
    private Integer cameraId;

    @Column(name = "gesture_name", nullable = false)
    private String gestureName;

    @Column(name = "gesture_type")
    private String gestureType;

    @Column(name = "gesture_description", columnDefinition = "text")
    private String gestureDescription;

    @Column(name = "geture_image_path")
    private String gestureImagePath;

    @Column(name = "is_enabled")
    private String isEnabled;

    @Column(name = "action_id", nullable = false)
    private Integer actionId;
}