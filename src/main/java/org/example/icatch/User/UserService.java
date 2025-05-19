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
            // 1. 알림 삭제
            int notificationDeleted = jdbcTemplate.update("DELETE FROM notification WHERE user_id = ?", userId);
            System.out.println("알림 테이블에서 삭제된 레코드 수: " + notificationDeleted);

            // 2. 카메라 삭제
            int cameraDeleted = jdbcTemplate.update("DELETE FROM camera WHERE user_id = ?", userId);
            System.out.println("카메라 테이블에서 삭제된 레코드 수: " + cameraDeleted);

            // 3. 디바이스에 연결된 사진 삭제
            List<Integer> deviceIds = jdbcTemplate.queryForList(
                    "SELECT device_id FROM device WHERE user_id = ?", Integer.class, userId);

            int pictureDeleted = 0;
            for (Integer deviceId : deviceIds) {
                pictureDeleted += jdbcTemplate.update("DELETE FROM picture WHERE device_id = ?", deviceId);
            }
            System.out.println("사진 테이블에서 삭제된 레코드 수: " + pictureDeleted);

            // 4. 디바이스 삭제
            int deviceDeleted = jdbcTemplate.update("DELETE FROM device WHERE user_id = ?", userId);
            System.out.println("디바이스 테이블에서 삭제된 레코드 수: " + deviceDeleted);

            // 5. 설정 삭제
            int settingDeleted = jdbcTemplate.update("DELETE FROM setting WHERE user_id = ?", userId);
            System.out.println("설정 테이블에서 삭제된 레코드 수: " + settingDeleted);

            // 6. 타겟 테이블 삭제 (오류 메시지에서 확인된 새로운 단계)
            int targetDeleted = jdbcTemplate.update("DELETE FROM target WHERE user_id = ?", userId);
            System.out.println("타겟 테이블에서 삭제된 레코드 수: " + targetDeleted);

            // 7. 사용자 삭제
            int userDeleted = jdbcTemplate.update("DELETE FROM user WHERE user_id = ?", userId);

            if (userDeleted == 0) {
                throw new RuntimeException("사용자 삭제 실패: 사용자 테이블에서 레코드가 삭제되지 않았습니다.");
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