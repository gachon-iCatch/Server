package org.example.icatch.service;

import org.example.icatch.dto.*;
import org.example.icatch.model.*;
import org.example.icatch.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CameraRepository cameraRepository;
    private final TargetRepository targetRepository;
    private final GestureRepository gestureRepository;
    private final ActiveLogRepository activeLogRepository; // 추가

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       CameraRepository cameraRepository, TargetRepository targetRepository,
                       GestureRepository gestureRepository, ActiveLogRepository activeLogRepository) { // 생성자 파라미터 추가
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cameraRepository = cameraRepository;
        this.targetRepository = targetRepository;
        this.gestureRepository = gestureRepository;
        this.activeLogRepository = activeLogRepository;
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
}