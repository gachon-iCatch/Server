package org.example.icatch.Model;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ModelDto {
    private String version;
    private String nickname;
    private LocalDateTime createdAt;
    public ModelDto(LocalDateTime createdAt, String nickname, String version) {
    this.createdAt = createdAt;
    this.nickname = nickname;
    this.version = version;
}
}