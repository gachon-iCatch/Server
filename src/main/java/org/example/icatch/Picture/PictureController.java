package org.example.icatch.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monitoring/pictures")
public class PictureController {

    // 실제 이미지가 저장된 디렉토리 경로로 변경 필요
    private final String uploadDir = "/path/to/images/";

    // 사용자별 이미지 목록 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getPicturesByUserId(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 디렉토리에서 이미지 파일 목록 가져오기
            List<Map<String, Object>> pictures = getImagesFromDirectory(userId);

            response.put("success", true);
            response.put("pictures", pictures);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 이미지 파일 자체를 서빙하는 엔드포인트
    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                String contentType = Files.probeContentType(filePath);
                if(contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 특정 사용자의 이미지 파일 목록을 디렉토리에서 가져오는 메서드
    private List<Map<String, Object>> getImagesFromDirectory(Integer userId) {
        List<Map<String, Object>> images = new ArrayList<>();

        // 여기서는 예시로 모든 이미지를 가져오지만,
        // 실제로는 사용자 ID에 따라 필터링 필요
        File directory = new File(uploadDir);
        if (directory.exists() && directory.isDirectory()) {
            // 이미지 파일만 필터링 (확장자로)
            File[] files = directory.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") ||
                            name.toLowerCase().endsWith(".jpeg") ||
                            name.toLowerCase().endsWith(".png"));

            if (files != null) {
                for (File file : files) {
                    Map<String, Object> imageInfo = new HashMap<>();
                    imageInfo.put("fileName", file.getName());
                    imageInfo.put("imageUrl", "/api/monitoring/pictures/image/" + file.getName());
                    imageInfo.put("lastModified", file.lastModified());
                    imageInfo.put("size", file.length());

                    images.add(imageInfo);
                }
            }
        }

        return images;
    }
}