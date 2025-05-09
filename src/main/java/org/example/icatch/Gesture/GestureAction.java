package org.example.icatch.Gesture;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gesture_actions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GestureAction{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Integer actionId;

    @Column(name = "black_screen")
    @Enumerated(EnumType.STRING)
    private EnabledStatus blackScreen = EnabledStatus.disabled;

    @Column(name = "capture")
    @Enumerated(EnumType.STRING)
    private EnabledStatus capture = EnabledStatus.disabled;

    @Column(name = "send_alert")
    @Enumerated(EnumType.STRING)
    private EnabledStatus sendAlert = EnabledStatus.disabled;

    @Column(name = "notifications")
    @Enumerated(EnumType.STRING)
    private EnabledStatus notifications = EnabledStatus.disabled;


    @Column(name = "massage")
    private String message;

    @Transient  // DB에 저장되지 않는 필드
    private SelectedFunction selectedFunction;

    public enum EnabledStatus {
        enabled, disabled
    }

    public enum SelectedFunction {
        BLACK_SCREEN("블랙 스크린 ON/OFF"),
        SIGNAL("신고 기능"),
        TIME_CAPTURE("시간 찍기"),
        ALARM("알림 ON/OFF"),
        FINE_TEXT("\"괜찮아~\" 알림 보내기"),
        EMERGENCY_TEXT("\"도와줘!\" 알림 보내기"),
        HELP_TEXT("\"불편해 ㅠㅠ\" 알림 보내기"),
        PERSON_TEXT("\"인사하기\" 알림 보내기");

        private String description;

        SelectedFunction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public void applySelectedFunction(SelectedFunction function) {
        this.selectedFunction = function;

        // 모든 값을 기본적으로 비활성화
        this.blackScreen = EnabledStatus.disabled;
        this.capture = EnabledStatus.disabled;
        this.sendAlert = EnabledStatus.disabled;
        this.notifications = EnabledStatus.disabled;

        // 메시지 초기화 (기본 메시지 설정)
        switch (function) {
            case BLACK_SCREEN:
                this.blackScreen = EnabledStatus.enabled;
                this.message = "블랙 스크린 활성화";
                break;
            case SIGNAL:
                this.sendAlert = EnabledStatus.enabled;
                this.message = "신고가 접수되었습니다";
                break;
            case TIME_CAPTURE:
                this.capture = EnabledStatus.enabled;
                this.message = "화면이 캡처되었습니다";
                break;
            case ALARM:
                this.notifications = EnabledStatus.enabled;
                this.message = "알림이 활성화되었습니다";
                break;
            case FINE_TEXT:
                this.message = "괜찮아~";
                break;
            case EMERGENCY_TEXT:
                this.message = "도와줘!";
                break;
            case HELP_TEXT:
                this.message = "불편해 ㅠㅠ";
                break;
            case PERSON_TEXT:
                this.message = "인사하기";
                break;
        }
    }
    @PostLoad
    public void initSelectedFunction() {
        if (this.blackScreen == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.BLACK_SCREEN;
        } else if (this.sendAlert == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.SIGNAL;
        } else if (this.capture == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.TIME_CAPTURE;
        } else if (this.notifications == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.ALARM;
        } else if (message != null && !message.isEmpty()) {
            // 메시지 내용에 따라 적절한 텍스트 기능 설정
            if (message.contains("괜찮아")) {
                this.selectedFunction = SelectedFunction.FINE_TEXT;
            } else if (message.contains("도와줘")) {
                this.selectedFunction = SelectedFunction.EMERGENCY_TEXT;
            } else if (message.contains("불편해")) {
                this.selectedFunction = SelectedFunction.HELP_TEXT;
            } else if (message.contains("인사하기")) {
                this.selectedFunction = SelectedFunction.PERSON_TEXT;
            }
        }
    }

    // 기존 setter 수정
    public void setSelectedFunction(SelectedFunction selectedFunction) {
        this.selectedFunction = selectedFunction;
        if (selectedFunction != null) {
            applySelectedFunction(selectedFunction);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}