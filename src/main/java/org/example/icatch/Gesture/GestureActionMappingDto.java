
package org.example.icatch.Gesture;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GestureActionMappingDto {
    private Integer userId;
    private List<GestureMappingItem> gestures = new ArrayList<>();

    @Data
    public static class GestureMappingItem {
        private String gestureId;
        private String actionId;
    }
}