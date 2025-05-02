package org.example.icatch.Admin;

import jakarta.validation.Valid;
import org.example.icatch.User.AuthResponse;
import org.example.icatch.User.LoginRequest;
import org.example.icatch.User.User;
import org.example.icatch.security.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = adminService.login(loginRequest);
            if (authResponse.isAdmin()) {
                return ResponseEntity.ok(ApiResponse.success("로그인 성공", authResponse));
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인 실패: " + e.getMessage()));
        }
    }

    @GetMapping("users")
    public ResponseEntity<ApiResponse> getUsers() {
        try {
            List<UsersResponse> usersResponse = adminService.getUsers();
            return ResponseEntity.ok(ApiResponse.success("사용자 정보", usersResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("사용자 정보 가져오기 실패: " + e.getMessage()));
        }
    }
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam("userId") Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("유저 삭제 완료"));
    }
}
