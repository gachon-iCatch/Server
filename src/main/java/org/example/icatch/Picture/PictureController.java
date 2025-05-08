package org.example.icatch.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monitoring/pictures")
public class PictureController {

    private final PictureService pictureService;

    @Autowired
    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    // 특정 사용자의 모든 이미지 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPicturesByUserId(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Picture> pictures = pictureService.getPicturesByUserId(userId);
            List<PictureResponseDTO> pictureDTOs = pictures.stream()
                    .map(PictureResponseDTO::new)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("pictures", pictureDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 특정 카메라의 이미지 목록 조회
    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<?> getPicturesByCameraId(@PathVariable Integer cameraId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Picture> pictures = pictureService.getPicturesByDeviceId(cameraId);
            List<PictureResponseDTO> pictureDTOs = pictures.stream()
                    .map(PictureResponseDTO::new)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("pictures", pictureDTOs);
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

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 이미지 타입에 따라 수정 필요
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}