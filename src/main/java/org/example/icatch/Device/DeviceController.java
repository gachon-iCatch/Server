package org.example.icatch.Device;


import org.example.icatch.security.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

   @GetMapping("/auth/authenticate")   //Post ->Get 으로 변경함.
    public ResponseEntity<ApiResponse> authenticate(@RequestBody DeviceAuthRequest deviceAuthRequest) {

        try{
            // false를 두 번째 매개변수로 추가 (설문조사 아님)
            DeviceAuthResponse data = deviceService.registerDevice(deviceAuthRequest, false);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("디바이스 등록이 완료되었습니다", data));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }



    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse> registerDevice(
            @RequestBody DeviceAuthRequest deviceAuthRequest,
            @RequestParam(required = false) Boolean isSurvey) {

        try {
            DeviceAuthResponse data = deviceService.registerDevice(deviceAuthRequest, isSurvey);

            String message;
            if (isSurvey != null && isSurvey == true) {
                message = "설문조사 디바이스 등록이 완료되었습니다";
            } else {
                message = "디바이스 등록이 완료되었습니다";
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(message, data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}