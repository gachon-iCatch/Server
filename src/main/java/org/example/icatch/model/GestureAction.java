package org.example.icatch.model;

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
public class GestureAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Integer actionId;

    @Column(name = "selected_function")
    @Enumerated(EnumType.STRING)
    private SelectedFunction selectedFunction;

    @Column(name = "message")
    private String message;

    // 선택 가능한 기능들을 정의한 열거형
    public enum SelectedFunction {
        BLACK_SCREEN("블랙 스크린 ON/OFF"),
        SIGNAL("신고 기능"),
        TIME_CAPTURE("시간 찍기"),
        ALARM("알림 ON/OFF"),
        BLACK_TEXT("\"블랙아웃~\" 알림 보내기"),
        EMERGENCY_TEXT("\"도와줘!\" 알림 보내기"),
        HELP_TEXT("\"불편해 ㅠㅠ\" 알림 보내기"),
        PERSON_TEXT("\"인사하기😊\" 알림 보내기"),
        NONE("선택 안함");

        private String description;

        SelectedFunction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}