package main.domain.usecase;

import main.domain.dto.SettingsDto;
import main.domain.model.GlobalSetting;
import main.domain.model.User;
import main.domain.port.GlobalSettingRepPort;
import main.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class GlobalSettingUseCase {
    private final GlobalSettingRepPort settingRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public GlobalSettingUseCase(GlobalSettingRepPort settingRepositoryPort,
                                UserRepositoryPort userRepositoryPort) {
        this.settingRepositoryPort = settingRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    public boolean isStatisticsPublic() {
        Optional<GlobalSetting> globalSetting = settingRepositoryPort.findByCodeEquals("STATISTICS_IS_PUBLIC");
        return globalSetting.isPresent() && !globalSetting.get().getValue().equals("NO");
    }

    public LinkedHashMap<String, Boolean> getSettings() {
        Iterable<GlobalSetting> globalSettings = settingRepositoryPort.findAll();
        LinkedHashMap<String, Boolean> settings = new LinkedHashMap<>();
        for (GlobalSetting setting : globalSettings) {
            settings.put(setting.getCode(), setting.getValue().equals("YES"));
        }
        return settings;
    }

    public void saveSettings(SettingsDto settings, int userId) {
        Optional<User> findUser = userRepositoryPort.findById(userId);
        if (findUser.isPresent()) {
            User user = findUser.get();
            if (user.isModerator()) {
                Iterable<GlobalSetting> globalSettings = settingRepositoryPort.findAll();
                for (GlobalSetting setting : globalSettings) {
                    switch (setting.getCode()) {
                        case "MULTIUSER_MODE":
                            setting.setValue(settings.getMultiUserMode() ? "YES" : "NO");
                            break;
                        case "POST_PREMODERATION":
                            setting.setValue(settings.getPostPreModeration() ? "YES" : "NO");
                            break;
                        case "STATISTICS_IS_PUBLIC":
                            setting.setValue(settings.getStatisticsIsPublic() ? "YES" : "NO");
                            break;
                    }
                }
                settingRepositoryPort.saveAll(globalSettings);
            }
        }
    }
}