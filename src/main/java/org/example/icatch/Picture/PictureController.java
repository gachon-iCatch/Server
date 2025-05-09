package org.example.icatch.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.nio.file.Files;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/pictures")
@CrossOrigin(origins = "*")
public class PictureController {

    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    // 사용자별
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getPicturesByUserId(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<PictureResponseDTO> pictures = pictureService.getPicturesByUserId(userId);

            response.put("success", true);
            response.put("pictures", pictures);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 디바이스별 이미지 목록 조회
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<?> getPicturesByDeviceId(@PathVariable Integer deviceId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<PictureResponseDTO> pictures = pictureService.getPicturesByDeviceId(deviceId);

            response.put("success", true);
            response.put("pictures", pictures);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 이미지 상세 정보 조회
    @GetMapping("/detail/{imageId}")
    public ResponseEntity<?> getPictureDetail(@PathVariable Integer imageId) {
        Map<String, Object> response = new HashMap<>();

        try {
            PictureResponseDTO picture = pictureService.getPictureById(imageId);

            response.put("success", true);
            response.put("picture", picture);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 이미지 파일 자체를 서빙하는 엔드포인트
    @GetMapping("/image/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Integer imageId) {
        try {
            Resource resource = pictureService.loadImageAsResource(imageId);

            String contentType = Files.probeContentType(resource.getFile().toPath());
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 이미지 삭제
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deletePicture(@PathVariable Integer imageId) {
        Map<String, Object> response = new HashMap<>();

        try {
            pictureService.deletePicture(imageId);

            response.put("success", true);
            response.put("message", "이미지가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
