package org.example.icatch.Admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import java.util.List;

import org.example.icatch.Admin.AlarmResponse; 
import org.example.icatch.security.ApiResponse;         
import org.example.icatch.Admin.AlarmService; 

@RestController
@RequestMapping("/api/admin")
public class AlarmController {
    private final AlarmService alarmService;
    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }
    @GetMapping("/notifications") 
    public ResponseEntity<ApiResponse> getAlarms(){
        try{
            List<AlarmResponse> alarmResponse = alarmService.getAlarms();
            return ResponseEntity.ok(ApiResponse.success("알림 정보", alarmResponse));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("알림 정보 가져오기 실패: " + e.getMessage()));
        }
    }
    @PostMapping("/notifications")
    public ResponseEntity<ApiResponse> postAlarm(@RequestBody @Valid AlarmRequest AlarmRequest){
        try{
            String alarmResponse = alarmService.postAlarm(AlarmRequest);
            return ResponseEntity.ok(ApiResponse.success("알림 정보", alarmResponse));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("알림 전송 실패: " + e.getMessage()));
        }
    }
    @DeleteMapping("/notifications")
    public ResponseEntity<ApiResponse> deleteAlarm(@RequestParam("userId") Integer userId){
        try{
            alarmService.deleteAlarm(userId);
            return ResponseEntity.ok(ApiResponse.success("삭제되었습니다"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("알림 전송 실패: " + e.getMessage()));
        }
    }
}