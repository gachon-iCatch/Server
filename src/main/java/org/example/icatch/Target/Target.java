package org.example.icatch.Target;


import jakarta.persistence.*;

@Entity
@Table(name = "target")
public class Target {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Integer targetId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private TargetType targetType;

    // 생성자
    public Target() {
    }

    // Builder 패턴을 위한 정적 내부 클래스
    public static Builder builder() {
        return new Builder();
    }

    // Getter와 Setter
    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    // Target 타입 열거형
    public enum TargetType {
        person,
        pet
    }

    // Builder 클래스
    public static class Builder {
        private Target target = new Target();

        public Builder userId(Integer userId) {
            target.setUserId(userId);
            return this;
        }

        public Builder targetType(TargetType targetType) {
            target.setTargetType(targetType);
            return this;
        }

        public Target build() {
            return target;
        }
    }
}