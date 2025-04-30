package org.example.icatch.User;

import jakarta.validation.Valid;
import org.example.icatch.security.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            userService.changePassword(email, passwordChangeRequest);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            userService.deleteUser(email);
            return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}