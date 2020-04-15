package main.repository;

import main.model.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalSettingRepository extends CrudRepository<GlobalSetting, Integer> {
    Optional<GlobalSetting> findByCodeEquals(String code);
}
