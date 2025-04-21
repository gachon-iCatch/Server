package org.example.icatch.dto;

import lombok.Data;
import org.example.icatch.model.GestureAction;

@Data
public class GestureActionDto {
    private Integer gestureId;
    private Integer userId;
    private Integer cameraId;
    private String gestureName;
    private String gestureType;
    private String gestureDescription;
    private String gestureImagePath;
    private String isEnabled;
    private GestureAction.SelectedFunction selectedFunction;
    private String message;
}