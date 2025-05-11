package org.example.icatch.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Transactional(readOnly = true)
    public boolean isNotificationEnabled(Integer userId) {
        return settingRepository.findByUserId(userId)
                .map(setting -> setting.getNotificationEnabled() == Setting.NotificationEnabled.enabled)
                .orElse(false); // 설정이 없으면 기본적으로 비활성화
    }
}