package org.example.icatch.Admin;

import org.example.icatch.User.AuthResponse;
import org.example.icatch.User.LoginRequest;
import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
import org.example.icatch.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.createToken(loginRequest.getEmail());


        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .userNickname(user.getUserNickname())
                .isAdmin(user.getIsAdmin())
                .build();
    }

    public List<UsersResponse> getUsers() {
        List<User> users = userRepository.findAll();

        List<UsersResponse> userResponse = users.stream()
                .map(user -> new UsersResponse(user.getEmail(), user.getUserNickname()))
                .collect(Collectors.toList());

        return userResponse;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
