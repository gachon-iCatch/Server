package org.example.icatch.Picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Integer> {

    // 디바이스 ID로 이미지 검색
    List<Picture> findByDevice_DeviceIdOrderByCaptureTimeDesc(Integer deviceId);

    // 사용자 ID로 이미지 검색 (직접적인 관계가 없을 경우 JPQL 쿼리로 작성)
    @Query("SELECT p FROM Picture p JOIN p.device d JOIN d.user u WHERE u.userId = :userId ORDER BY p.captureTime DESC")
    List<Picture> findByUserIdOrderByCaptureTimeDesc(@Param("userId") Integer userId);
}