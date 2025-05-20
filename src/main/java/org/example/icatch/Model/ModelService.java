package org.example.icatch.Model;

import org.example.icatch.Device.Device;
import org.example.icatch.Device.DeviceRepository;
import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.icatch.Notification.NotificationRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelService {
    private final ModelRepository modelRepository;
    public ModelService(ModelRepository modelRepository){
        this.modelRepository = modelRepository;
    }
    public void update(ModelDto modelDto){
        Model model = new Model();
        model.setVersion(modelDto.getVersion());
        model.setCreatedAt(LocalDateTime.now());  // 현재 시간 설정
        modelRepository.save(model);
    }
    public List<ModelDto> getUpdates(){
        List<Model> models = modelRepository.findAll();
        List<ModelDto> modelDto = models.stream()
            .map(model -> new ModelDto(model.getCreatedAt(), model.getNickname(), model.getVersion()))
            .collect(Collectors.toList());

        return modelDto;
    }
}
