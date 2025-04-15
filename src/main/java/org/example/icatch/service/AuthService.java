package org.example.icatch.service;

import org.example.icatch.dto.AuthResponse;
import org.example.icatch.dto.LoginRequest;
import org.example.icatch.model.User;
import org.example.icatch.repository.UserRepository;
import org.example.icatch.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(loginRequest.getEmail());

        // 사용자 정보 조회
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 응답 생성
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                . userNickname(user.getUserNickname())
                .build();
    }
}