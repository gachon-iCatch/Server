package org.example.icatch.ActiveLog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.icatch.Camera.Camera;

@Entity
@Table(name = "active_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;

    @Column(name = "log_text")
    private String logText;

    @Column(name = "thumbimage_path")
    private String thumbimagePath;

    @Column(name = "created_at")
    private String createdAt;
}