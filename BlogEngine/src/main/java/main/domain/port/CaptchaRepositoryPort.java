package main.domain.port;

import main.domain.model.CaptchaCode;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CaptchaRepositoryPort {
    Optional<CaptchaCode> findBySecretCode(String secretCode);

    void deleteOldCaptcha(LocalDateTime time);

    CaptchaCode save(CaptchaCode captchaCode);
}