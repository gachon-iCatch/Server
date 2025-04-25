package org.example.icatch.service;

import org.example.icatch.dto.TargetCreateRequest;
import org.example.icatch.model.Target;
import org.example.icatch.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .userId(request.getUser_Id())
                .cameraName(request.getCamera_name()) // 카메라 이름만 설정
                .targetType(Target.TargetType.valueOf(request.getTarget_Type().toLowerCase()))
                .build();

        Target savedTarget = targetRepository.save(target);

        return savedTarget.getTargetId();
    }
}