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
public class GestureAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Integer actionId;

    @Column(name = "selected_function")
    @Enumerated(EnumType.STRING)
    private GestureAction.SelectedFunction selectedFunction;

    @Column(name = "message")
    private String message;

    public enum SelectedFunction {
        BLACK_SCREEN("블랙 스크린 ON/OFF"),
        SIGNAL("신고 기능"),
        TIME_CAPTURE("시간 찍기"),
        ALARM("알림 ON/OFF"),
        FINE_TEXT("\"괜찮아~\" 알림 보내기"),
        EMERGENCY_TEXT("\"도와줘!\" 알림 보내기"),
        HELP_TEXT("\"불편해 ㅠㅠ\" 알림 보내기"),
        PERSON_TEXT("\"인사하기😊\" 알림 보내기");

        private String description;

        SelectedFunction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}