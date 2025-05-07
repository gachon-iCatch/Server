package org.example.icatch.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TargetService {

    private final TargetRepository targetRepository;

    @Autowired
    public TargetService(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    @Transactional
    public Integer createTarget(TargetCreateRequest request) {
        Target target = Target.builder()
                .userId(request.getUserId())
                .targetType(Target.TargetType.valueOf(request.getTargetType().toLowerCase()))
                .build();

        Target savedTarget = targetRepository.save(target);

        return savedTarget.getTargetId();
    }

    // 사용자  id로 타겟 목록 조회
    @Transactional(readOnly = true)
    public List<Target> getTargetsByUserId(Integer userId) {
        return targetRepository.findByUserId(userId);
    }

    // 타겟 삭제
    @Transactional
    public void deleteTarget(Integer targetId) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("해당 타겟을 찾을 수 없습니다."));

        targetRepository.delete(target);
    }

    // 타겟 업데이트
    @Transactional
    public void updateTarget(Integer targetId, TargetUpdateRequest request) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("해당 타겟을 찾을 수 없습니다."));

        if (request.getTargetType() != null && !request.getTargetType().isEmpty()) {
            target.setTargetType(Target.TargetType.valueOf(request.getTargetType().toLowerCase()));
        }

        targetRepository.save(target);
    }
}