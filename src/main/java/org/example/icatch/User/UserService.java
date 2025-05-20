package org.example.icatch.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        if (userRepository.existsByUserNickname(signupRequest.getUserNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserNickname(signupRequest.getUserNickname());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(String email, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인용 비밀번호가 일치하지 않습니다");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        entityManager.flush();
        entityManager.clear();

        // 외래 키 제약 조건을 우회하는 대체 방법
        user = entityManager.find(User.class, user.getUserId());
        if (user != null) {
            entityManager.remove(user);
        }
    }
    @Transactional
    public void deleteUserWithRelatedData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        Integer userId = user.getUserId();
        System.out.println("사용자 ID: " + userId + "를 삭제 시도합니다.");

        try {
            // 1. DB 트랜잭션 직접 제어를 위한 세션 플러시
            entityManager.flush();
            entityManager.clear();

            // 2. 먼저 notification 테이블 삭제
            System.out.println("Notification 삭제 시작...");
            jdbcTemplate.update("DELETE FROM notification WHERE user_id = ?", userId);
            jdbcTemplate.update(
                    "DELETE FROM notification WHERE camera_id IN (SELECT camera_id FROM camera WHERE user_id = ?)",
                    userId
            );
            System.out.println("Notification 삭제 완료");

            // 3. target_id 수집
            System.out.println("Target ID 수집...");
            List<Integer> targetIds;
            try {
                targetIds = jdbcTemplate.queryForList(
                        "SELECT DISTINCT target_id FROM camera WHERE user_id = ? AND target_id IS NOT NULL",
                        Integer.class, userId);
                System.out.println("수집된 Target ID: " + targetIds.size() + "개");
            } catch (Exception e) {
                System.out.println("Target ID 수집 중 오류: " + e.getMessage());
                targetIds = List.of();
            }

            // 4. Camera 삭제
            System.out.println("Camera 삭제 시작...");
            jdbcTemplate.update("DELETE FROM camera WHERE user_id = ?", userId);
            System.out.println("Camera 삭제 완료");

            // 5. Picture 삭제
            System.out.println("Picture 삭제 시작...");
            jdbcTemplate.update(
                    "DELETE FROM picture WHERE device_id IN (SELECT device_id FROM device WHERE user_id = ?)",
                    userId
            );
            System.out.println("Picture 삭제 완료");

            // 6. Device 삭제
            System.out.println("Device 삭제 시작...");
            jdbcTemplate.update("DELETE FROM device WHERE user_id = ?", userId);
            System.out.println("Device 삭제 완료");

            // 7. Target 직접 삭제 (중요)
            System.out.println("Target 직접 삭제 시작...");
            // 사용자 ID와 관련된 모든 target 레코드 조회
            List<Integer> userTargets = jdbcTemplate.queryForList(
                    "SELECT target_id FROM target WHERE user_id = ?", Integer.class, userId);
            System.out.println("사용자 관련 Target: " + userTargets.size() + "개");

            // 모든 target 삭제 전에 추가 참조 확인
            for (Integer targetId : userTargets) {
                // 다른 카메라에서 참조하는지 확인
                int referenceCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM camera WHERE target_id = ?", Integer.class, targetId);

                if (referenceCount > 0) {
                    System.out.println("Target ID " + targetId + "는 아직 " + referenceCount + "개의 카메라에서 참조 중");
                    // 참조를 NULL로 설정 (카메라에서 이 target 연결 해제)
                    jdbcTemplate.update("UPDATE camera SET target_id = NULL WHERE target_id = ?", targetId);
                }

                // 이제 안전하게 target 삭제
                int deleted = jdbcTemplate.update("DELETE FROM target WHERE target_id = ?", targetId);
                System.out.println("Target ID " + targetId + " 삭제 결과: " + deleted);
            }

            // 사용자 ID로 직접 타겟 삭제 시도 (중복 삭제일 수 있음)
            int targetDeleted = jdbcTemplate.update("DELETE FROM target WHERE user_id = ?", userId);
            System.out.println("사용자 ID로 직접 Target 삭제: " + targetDeleted + "개");

            // 8. 고아 Target 정리
            System.out.println("고아 Target 정리 시작...");
            int orphanedTargetsDeleted = 0;
            for (Integer targetId : targetIds) {
                try {
                    int referenceCount = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM camera WHERE target_id = ?", Integer.class, targetId);

                    if (referenceCount == 0) {
                        int deleted = jdbcTemplate.update("DELETE FROM target WHERE target_id = ?", targetId);
                        orphanedTargetsDeleted += deleted;
                        System.out.println("고아 Target ID " + targetId + " 삭제: " + deleted);
                    }
                } catch (Exception e) {
                    System.out.println("Target ID " + targetId + " 정리 중 오류: " + e.getMessage());
                }
            }
            System.out.println("고아 Target 정리 완료: " + orphanedTargetsDeleted + "개");

            // 9. 사용자 삭제
            System.out.println("사용자 삭제 시작...");
            int userDeleted = jdbcTemplate.update("DELETE FROM user WHERE user_id = ?", userId);
            System.out.println("사용자 삭제 결과: " + userDeleted);

            if (userDeleted == 0) {
                throw new RuntimeException("사용자 삭제 실패");
            }

            System.out.println("사용자 ID: " + userId + "가 성공적으로 삭제되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("사용자 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }


    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    @Transactional
    public User completeSurvey(Integer userId) {
        User user = userRepository.findById(Integer.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setSurveyCompleted(true);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
    }
}