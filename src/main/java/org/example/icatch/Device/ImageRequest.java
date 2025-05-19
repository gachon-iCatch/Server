package org.example.icatch.Device;
import java.time.LocalDateTime;
import lombok.Data;
import java.util.Date;

@Data
public class ImageRequest {
    private Long deviceId;
    private Integer userId;
    private Date timestamp;
}
