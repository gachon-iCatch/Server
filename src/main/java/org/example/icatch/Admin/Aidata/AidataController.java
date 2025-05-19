package org.example.icatch.Admin.Aidata;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/aidata")
public class AidataController {
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> getAidataFilePairs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String imageDirPath = "/home/t25104/aidata/dataset/train/images/";
        String labelDirPath = "/home/t25104/aidata/dataset/train/labels/";

        File imageDir = new File(imageDirPath);
        File labelDir = new File(labelDirPath);

        if (!imageDir.exists() || !labelDir.exists()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "message", "이미지 또는 라벨 디렉토리가 존재하지 않습니다."
            ));
        }

        File[] imageFiles = imageDir.listFiles((dir, name) ->
                name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
        );

        if (imageFiles == null) imageFiles = new File[0];

        int total = imageFiles.length;
        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);

        List<Map<String, Object>> filePairs = Arrays.stream(imageFiles)
                .sorted()
                .skip(from)
                .limit(size)
                .map(img -> {
                    String name = img.getName();
                    String baseName = name.substring(0, name.lastIndexOf('.'));
                    File labelFile = new File(labelDirPath + baseName + ".txt");

                    String labelText = null;
                    List<Map<String, Object>> parsedLabels = null;

                    if (labelFile.exists()) {
                        try {
                            labelText = Files.readString(labelFile.toPath());

                            parsedLabels = Arrays.stream(labelText.split("\\R")) // 줄 단위 분할
                                    .filter(line -> !line.isBlank())
                                    .map(line -> {
                                        String[] parts = line.trim().split("\\s+");
                                        if (parts.length < 5) return null;

                                        int clsId = Integer.parseInt(parts[0]);
                                        String clsName = switch (clsId) {
                                            case 0 -> "cat";
                                            case 1 -> "dog";
                                            case 2 -> "person";
                                            default -> "unknown";
                                        };

                                        Map<String, Object> labelMap = new HashMap<>();
                                        labelMap.put("class", clsName);
                                        labelMap.put("x", Double.parseDouble(parts[1]));
                                        labelMap.put("y", Double.parseDouble(parts[2]));
                                        labelMap.put("w", Double.parseDouble(parts[3]));
                                        labelMap.put("h", Double.parseDouble(parts[4]));
                                        return labelMap;
                                    })
                                    .filter(l -> l != null)
                                    .collect(Collectors.toList());

                        } catch (IOException e) {
                            labelText = null;
                        }
                    }

                    Map<String, Object> fileMap = new HashMap<>();
                    fileMap.put("image", name);
                    fileMap.put("label", baseName + ".txt");
                    fileMap.put("labelText", labelText);
                    fileMap.put("parsedLabels", parsedLabels);

                    return fileMap;
                })

                .collect(Collectors.toList());


        return ResponseEntity.ok(Map.of(
                "status", 200,
                "page", page,
                "size", size,
                "total", total,
                "files", filePairs
        ));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadAidataFile(
            @RequestParam String type,     // "images" 또는 "labels"
            @RequestParam String name      // 파일명, 예: "0001.jpg"
    ) {
        String basePath = "/home/t25104/aidata/dataset/train/";
        String folder = type.equals("labels") ? "labels/" : "images/";
        Path filePath = Paths.get(basePath + folder + name);

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String contentType = "application/octet-stream";
        if (type.equals("images")) {
            String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "jpeg";
            contentType = "image/" + ext;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    @GetMapping("/files/by-class")
    public ResponseEntity<Map<String, Object>> getAidataFilesByClass(
            @RequestParam String className,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String imageDirPath = "/home/t25104/aidata/dataset/train/images/";
        String labelDirPath = "/home/t25104/aidata/dataset/train/labels/";

        File imageDir = new File(imageDirPath);
        File labelDir = new File(labelDirPath);

        if (!imageDir.exists() || !labelDir.exists()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "message", "이미지 또는 라벨 디렉토리가 존재하지 않습니다."
            ));
        }

        File[] imageFiles = imageDir.listFiles((dir, name) ->
                name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
        );

        if (imageFiles == null) imageFiles = new File[0];

        // 전체 후보 파싱 + 필터링
        List<Map<String, Object>> filteredFiles = Arrays.stream(imageFiles)
                .sorted()
                .map(img -> {
                    String name = img.getName();
                    String baseName = name.substring(0, name.lastIndexOf('.'));
                    File labelFile = new File(labelDirPath + baseName + ".txt");

                    String labelText = null;
                    List<Map<String, Object>> parsedLabels = null;

                    if (labelFile.exists()) {
                        try {
                            labelText = Files.readString(labelFile.toPath());
                            parsedLabels = Arrays.stream(labelText.split("\\R"))
                                    .filter(line -> !line.isBlank())
                                    .map(line -> {
                                        String[] parts = line.trim().split("\\s+");
                                        if (parts.length < 5) return null;

                                        int clsId = Integer.parseInt(parts[0]);
                                        String clsName = switch (clsId) {
                                            case 0 -> "cat";
                                            case 1 -> "dog";
                                            case 2 -> "person";
                                            default -> "unknown";
                                        };

                                        Map<String, Object> labelMap = new HashMap<>();
                                        labelMap.put("class", clsName);
                                        labelMap.put("x", Double.parseDouble(parts[1]));
                                        labelMap.put("y", Double.parseDouble(parts[2]));
                                        labelMap.put("w", Double.parseDouble(parts[3]));
                                        labelMap.put("h", Double.parseDouble(parts[4]));
                                        return labelMap;
                                    })
                                    .filter(l -> l != null)
                                    .collect(Collectors.toList());
                        } catch (IOException e) {
                            labelText = null;
                        }
                    }

                    Map<String, Object> fileMap = new HashMap<>();
                    fileMap.put("image", name);
                    fileMap.put("label", baseName + ".txt");
                    fileMap.put("labelText", labelText);
                    fileMap.put("parsedLabels", parsedLabels);

                    return fileMap;
                })
                .filter(file -> {
                    List<Map<String, Object>> labels = (List<Map<String, Object>>) file.get("parsedLabels");
                    return labels != null &&
                            labels.stream().anyMatch(label -> className.equalsIgnoreCase((String) label.get("class")));
                })
                .collect(Collectors.toList());

        int total = filteredFiles.size();
        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);
        List<Map<String, Object>> pagedFiles = filteredFiles.subList(from, to);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "page", page,
                "size", size,
                "total", total,
                "files", pagedFiles
        ));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAidataFile(@RequestParam String name) {
        String imagePath = "/home/t25104/aidata/dataset/train/images/" + name;
        String baseName = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
        String labelPath = "/home/t25104/aidata/dataset/train/labels/" + baseName + ".txt";

        boolean deletedImage = new File(imagePath).delete();
        boolean deletedLabel = new File(labelPath).delete();

        if (deletedImage || deletedLabel) {
            return ResponseEntity.ok(Map.of("status", 200, "message", "삭제 완료"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "message", "파일이 존재하지 않거나 삭제 실패"
            ));
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAidata(
            @RequestParam("image") MultipartFile[] imageFiles,
            @RequestParam("label") MultipartFile[] labelFiles
    ) {
        String imageDir = "/home/t25104/aidata/dataset/train/images/";
        String labelDir = "/home/t25104/aidata/dataset/train/labels/";

        if (imageFiles.length != labelFiles.length) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "이미지와 라벨 파일 수가 일치하지 않습니다."
            ));
        }

        try {
            for (int i = 0; i < imageFiles.length; i++) {
                MultipartFile image = imageFiles[i];
                MultipartFile label = labelFiles[i];

                String imageName = image.getOriginalFilename();
                String labelName = label.getOriginalFilename();

                if (imageName == null || labelName == null) continue;

                // ✅ 클래스 ID 유효성 검사
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(label.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.isBlank()) continue;
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length < 5) continue;

                        int clsId = Integer.parseInt(parts[0]);
                        if (clsId < 0 || clsId > 2) {
                            return ResponseEntity.badRequest().body(Map.of(
                                    "status", 400,
                                    "message", String.format("라벨 파일 '%s'에 잘못된 클래스 ID (%d)가 포함되어 있습니다. [허용: 0, 1, 2]", labelName, clsId)
                            ));
                        }
                    }
                }

                // ✅ 저장
                image.transferTo(new File(imageDir + imageName));
                label.transferTo(new File(labelDir + labelName));
            }

            return ResponseEntity.ok(Map.of("status", 200, "message", "업로드 성공"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "message", "파일 저장 중 오류 발생",
                    "error", e.getMessage()
            ));
        }
    }





}
