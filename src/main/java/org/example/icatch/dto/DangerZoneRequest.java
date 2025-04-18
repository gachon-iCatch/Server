package org.example.icatch.dto;

import java.util.List;


public class DangerZoneRequest {
    private List<Integer> zones; // 사용자가 선택한 위험 구역 리스트 (1-9)

    public List<Integer> getZones() {
        return zones;
    }

    public void setZones(List<Integer> zones) {
        this.zones = zones;
    }
}