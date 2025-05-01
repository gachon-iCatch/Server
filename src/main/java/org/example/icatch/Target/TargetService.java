package org.example.icatch.Target;

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
                .userId(request.getUserId())
                .targetType(Target.TargetType.valueOf(request.getTargetType().toLowerCase()))
                .build();

        Target savedTarget = targetRepository.save(target);

        return savedTarget.getTargetId();
    }
}