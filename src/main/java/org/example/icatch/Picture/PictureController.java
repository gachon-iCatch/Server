package org.example.icatch.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.icatch.Picture.PictureRepository;
import java.util.stream.Collectors;

import java.nio.file.Files;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/pictures")
public class PictureController {

    private final PictureService pictureService;
    private final PictureRepository pictureRepository;

    @Autowired
    public PictureController(PictureService pictureService, PictureRepository pictureRepository) {
        this.pictureService = pictureService;
        this.pictureRepository = pictureRepository;
    }

    // 사용자별 이미지 목록 조회 - 사용 중인 API
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getPicturesByUserId(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 디버깅을 위한 로그 추가
            System.out.println("사용자 ID: " + userId + "에 대한 이미지 요청 받음");

            List<PictureResponseDTO> pictures = pictureService.getPicturesByUserId(userId);

            // 응답 내용 로깅
            System.out.println("사용자 ID: " + userId + "에 대한 이미지 응답 수: " + pictures.size());
            for (PictureResponseDTO pic : pictures) {
                System.out.println("이미지 ID: " + pic.getImageId() + ", 경로: " + pic.getImagePath());
            }

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

    // 이미지 파일 자체를 반환
    @GetMapping("/image/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Integer imageId, @RequestParam(required = false) Integer userId) {
        try {
            // 디버깅을 위한 로그 추가
            System.out.println("이미지 ID: " + imageId + " 요청 받음");

            Picture picture = pictureRepository.findById(imageId)
                    .orElseThrow(() -> new Exception("이미지를 찾을 수 없습니다."));

            // 사용자 ID 검증 (선택적)
            if (userId != null) {
                Integer pictureUserId = picture.getDevice().getUser().getUserId();
                if (!userId.equals(pictureUserId)) {
                    return ResponseEntity.status(403).build(); // 권한 없음
                }
            }

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
            System.out.println("이미지 로드 오류: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public List<PictureResponseDTO> getAllPictures() {
        return pictureRepository.findAll().stream()
                .map(PictureResponseDTO::new)
                .collect(Collectors.toList());
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