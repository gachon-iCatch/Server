package org.example.icatch.Target;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetUpdateRequest {
    @JsonProperty("targetType")
    private String targetType;

    public TargetUpdateRequest() {
    }

    public TargetUpdateRequest(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}