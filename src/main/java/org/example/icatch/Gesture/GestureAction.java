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

    // 기능 이름 변경
    public enum SelectedFunction {
        BLACK_SCREEN("블랙 스크린 ON/OFF"),
        DECLARATION("신고 기능"),
        PICTURE("시간 찍기"),
        OK("\"괜찮아~\" 알림 보내기"),
        HELP("\"도와줘!\" 알림 보내기"),
        INCONVENIENT("\"불편해 ㅠㅠ\" 알림 보내기"),
        HELLO("\"인사하기\" 알림 보내기");

        // ALARM enum 값 제거됨

        private String description;

        // 나머지 코드는 동일

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
            case DECLARATION:
                this.sendAlert = EnabledStatus.enabled;
                this.message = "신고가 접수되었습니다";
                break;
            case PICTURE:
                this.capture = EnabledStatus.enabled;
                this.message = "화면이 캡처되었습니다";
                break;
            // ALARM case 제거됨
            case OK:
                this.message = "괜찮아~";
                break;
            case HELP:
                this.message = "도와줘!";
                break;
            case INCONVENIENT:
                this.message = "불편해 ㅠㅠ";
                break;
            case HELLO:
                this.message = "인사하기";
                break;
        }
    }
    @PostLoad
    public void initSelectedFunction() {
        if (this.blackScreen == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.BLACK_SCREEN;
        } else if (this.sendAlert == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.DECLARATION;
        } else if (this.capture == EnabledStatus.enabled) {
            this.selectedFunction = SelectedFunction.PICTURE;
        } else if (this.notifications == EnabledStatus.enabled) {
            // notifications 값이 enabled인 경우 기본값으로 OK 설정 (또는 다른 적절한 값)
            this.selectedFunction = SelectedFunction.OK;
        } else if (message != null && !message.isEmpty()) {
            // 메시지 내용에 따라 적절한 텍스트 기능 설정
            if (message.contains("괜찮아")) {
                this.selectedFunction = SelectedFunction.OK;
            } else if (message.contains("도와줘")) {
                this.selectedFunction = SelectedFunction.HELP;
            } else if (message.contains("불편해")) {
                this.selectedFunction = SelectedFunction.INCONVENIENT;
            } else if (message.contains("인사하기")) {
                this.selectedFunction = SelectedFunction.HELLO;
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