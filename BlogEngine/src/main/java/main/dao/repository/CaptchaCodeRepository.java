package main.dao.repository;

import main.domain.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {
    Optional<CaptchaCode> findBySecretCodeEquals(String secretCode);

    @Modifying
    @Query("delete from CaptchaCode AS cc WHERE cc.time < :time")
    void deleteOldCaptcha(LocalDateTime time);
}
