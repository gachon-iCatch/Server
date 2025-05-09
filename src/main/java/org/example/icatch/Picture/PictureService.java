package org.example.icatch.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PictureService {
    private final PictureRepository pictureRepository;
    private final String uploadDir = "/home/t25104/Server/image"; // 실제 이미지 경로

    @Autowired
    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Transactional(readOnly = true)
    public List<PictureResponseDTO> getPicturesByUserId(Integer userId) {
        List<Picture> pictures = pictureRepository.findByUserIdOrderByCaptureTimeDesc(userId);
        return pictures.stream()
                .map(PictureResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PictureResponseDTO> getPicturesByDeviceId(Integer deviceId) {
        List<Picture> pictures = pictureRepository.findByDevice_DeviceIdOrderByCaptureTimeDesc(deviceId);
        return pictures.stream()
                .map(PictureResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PictureResponseDTO getPictureById(Integer imageId) {
        Picture picture = pictureRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));
        return new PictureResponseDTO(picture);
    }

    // 이미지 파일 자체를 로드하는 메서드
    public Resource loadImageAsResource(Integer imageId) throws Exception {
        try {
            Picture picture = pictureRepository.findById(imageId)
                    .orElseThrow(() -> new Exception("이미지를 찾을 수 없습니다."));

            Path filePath = Paths.get(uploadDir).resolve(picture.getImagePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                return resource;
            } else {
                throw new Exception("이미지 파일을 찾을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new Exception("이미지 파일 경로가 잘못되었습니다.", e);
        }
    }

    // 이미지 삭제 메서드
    @Transactional
    public void deletePicture(Integer imageId) throws Exception {
        Picture picture = pictureRepository.findById(imageId)
                .orElseThrow(() -> new Exception("이미지를 찾을 수 없습니다."));

        // 1. 파일 시스템에서 이미지 파일 삭제
        String imagePath = picture.getImagePath();
        Path filePath = Paths.get(uploadDir).resolve(imagePath).normalize();

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new Exception("이미지 파일 삭제 중 오류가 발생했습니다.", e);
        }

        // 2. 데이터베이스에서 이미지 정보 삭제
        pictureRepository.delete(picture);
    }
}