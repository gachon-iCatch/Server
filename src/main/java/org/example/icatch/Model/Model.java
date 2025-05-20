package org.example.icatch.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.icatch.User.User;
import org.example.icatch.Device.Device;

import java.time.LocalDateTime;
@Entity
@Table(name = "model")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Integer modelId;

    @Column(name = "version")
    private String  version;
    
    @Builder.Default
    @Column(name = "nickname")
    private String  nickname="ADMIN";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}