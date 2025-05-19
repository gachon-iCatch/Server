package org.example.icatch.User;

import jakarta.validation.Valid;
import org.example.icatch.security.ApiResponse;
import org.example.icatch.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider; // 추가

    // 생성자에 JwtTokenProvider 추가
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Bearer 접두사 제거
            String token = authHeader;
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            userService.changePassword(email, passwordChangeRequest);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteAccount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Bearer 접두사 제거
            String token = authHeader;
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);

            // 수정된 메서드 호출
            userService.deleteUserWithRelatedData(email);
            return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}