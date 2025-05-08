package org.example.icatch.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;
    private final String uploadDir = "/path/to/images/"; // 실제 이미지가 저장된 디렉토리 경로로 변경 필요

    @Autowired
    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @Transactional(readOnly = true)
    public List<Picture> getPicturesByUserId(Integer userId) {
        // 이 메서드는 사용자 ID를 기반으로 이미지를 찾아야 함
        // 데이터베이스 구조에 따라 쿼리가 달라질 수 있음
        // 예: pictureRepository.findByDeviceUserIdOrderByCaptureTimeDesc(userId);
        return pictureRepository.findByUserIdOrderByCaptureTimeDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Picture> getPicturesByDeviceId(Integer deviceId) {
        return pictureRepository.findByDeviceIdOrderByCaptureTimeDesc(deviceId);
    }

    // 이미지 ID로 이미지 파일 자체를 로드하는 메서드
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
}