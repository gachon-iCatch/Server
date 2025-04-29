package org.example.icatch.service;

import org.example.icatch.dto.*;
import org.example.icatch.model.*;
import org.example.icatch.repository.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CameraRepository cameraRepository;
    private final TargetRepository targetRepository;
    private final GestureRepository gestureRepository;
    private final ActiveLogRepository activeLogRepository;
    private final SettingRepository settingRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       CameraRepository cameraRepository, TargetRepository targetRepository,
                       GestureRepository gestureRepository, ActiveLogRepository activeLogRepository,
                       SettingRepository settingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cameraRepository = cameraRepository;
        this.targetRepository = targetRepository;
        this.gestureRepository = gestureRepository;
        this.activeLogRepository = activeLogRepository;
        this.settingRepository = settingRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다"));
    }

    public boolean isNotificationEnabled(Integer userId) {
        Optional<Setting> settingOpt = settingRepository.findByUserId(userId);

        if (settingOpt.isPresent()) {
            Setting setting = settingOpt.get();
            return "enabled".equals(setting.getNotificationEnabled().toString());
        } else {
            // 설정이 없는 경우 기본값으로 true 반환
            return true;
        }
    }

    @Transactional
    public void updateNotificationSetting(String email, boolean enabled) {
        User user = getUserByEmail(email);
        Optional<Setting> settingOpt = settingRepository.findByUserId(user.getUserId());

        Setting setting;
        if (settingOpt.isPresent()) {
            setting = settingOpt.get();
        } else {
            // 설정이 없는 경우 새로 생성
            setting = new Setting();
            setting.setUserId(user.getUserId());
        }

        // enabled에 따라 ENUM 값 설정
        Setting.NotificationEnabled notificationStatus = enabled ?
                Setting.NotificationEnabled.enabled : Setting.NotificationEnabled.disabled;

        setting.setNotificationEnabled(notificationStatus);
        settingRepository.save(setting);
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

        userRepository.delete(user);
    }

    public UserProfileDto getUserProfileData(String email) {
        User user = getUserByEmail(email);
        Integer userId = user.getUserId();

        // 사용자 프로필 정보 조회
        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setUserId(userId);
        profileDto.setUsernickname(user.getUserNickname());  // nickname -> usernickname으로 변경
        profileDto.setEmail(user.getEmail());

        // 카메라, 반려동물, 제스처의 개수 조회
        int cameraCount = cameraRepository.countByUserId_UserId(userId);
        int targetCount = targetRepository.countByUserId(userId);
        int gestureCount = gestureRepository.countByUserId(userId);

        profileDto.setCameraCount(cameraCount);
        profileDto.setTargetCount(targetCount);
        profileDto.setGestureCount(gestureCount);

        // 알림 설정 상태 조회
        boolean notificationEnabled = isNotificationEnabled(userId);
        profileDto.setNotificationEnabled(notificationEnabled);

        return profileDto;
    }

    public Map<String, Object> getUserSettingsData(String email) {
        User user = getUserByEmail(email);

        Map<String, Object> accountSettings = new HashMap<>();
        accountSettings.put("userId", user.getUserId());
        accountSettings.put("email", user.getEmail());

        return accountSettings;
    }
}