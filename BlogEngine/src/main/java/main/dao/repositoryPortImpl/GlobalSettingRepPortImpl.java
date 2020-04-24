package main.dao.repositoryPortImpl;

import main.dao.repository.GlobalSettingRepository;
import main.domain.model.GlobalSetting;
import main.domain.port.GlobalSettingRepPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GlobalSettingRepPortImpl implements GlobalSettingRepPort {
    private final GlobalSettingRepository settingRepository;

    public GlobalSettingRepPortImpl(GlobalSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public Optional<GlobalSetting> findByCodeEquals(String code) {
        return settingRepository.findByCodeEquals(code);
    }

    @Override
    public Iterable<GlobalSetting> findAll() {
        return settingRepository.findAll();
    }

    @Override
    public Iterable<GlobalSetting> saveAll(Iterable<GlobalSetting> globalSettings) {
        return settingRepository.saveAll(globalSettings);
    }
}