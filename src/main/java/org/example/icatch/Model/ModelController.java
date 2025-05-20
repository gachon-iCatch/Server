package org.example.icatch.Model;

import jakarta.validation.Valid;
import org.example.icatch.User.AuthResponse;
import org.example.icatch.Model.ModelDto;
import org.example.icatch.User.User;
import org.example.icatch.security.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/model")
public class ModelController{
    private final ModelService modelService;
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }
    @PostMapping("/update")
    public ResponseEntity<ApiResponse> update(@RequestBody @Valid ModelDto modelDto){
        try {
            modelService.update(modelDto);
            return ResponseEntity.ok(ApiResponse.success("성공하였습니다"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인 실패: " + e.getMessage()));
        }
    }
    @GetMapping("/update")
    public ResponseEntity<ApiResponse> getUpdates(){
        try {
            List<ModelDto> modelDto = modelService.getUpdates();
            return ResponseEntity.ok(ApiResponse.success("업데이트 정보", modelDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("정보 가져오기 실패: " + e.getMessage()));
        }
    }
}