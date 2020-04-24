package main.domain.port;

import main.domain.model.GlobalSetting;

import java.util.Optional;

public interface GlobalSettingRepPort {
    Optional<GlobalSetting> findByCodeEquals(String code);

    Iterable<GlobalSetting> findAll();

    Iterable<GlobalSetting> saveAll(Iterable<GlobalSetting> globalSettings);
}
