package main.dao.repositoryPortImpl;

import main.dao.repository.CaptchaCodeRepository;
import main.domain.model.CaptchaCode;
import main.domain.port.CaptchaRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CaptchaRepositoryPortImpl implements CaptchaRepositoryPort {
    private final CaptchaCodeRepository captchaRepository;

    public CaptchaRepositoryPortImpl(CaptchaCodeRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    @Override
    public Optional<CaptchaCode> findBySecretCode(String secretCode) {
        return captchaRepository.findBySecretCodeEquals(secretCode);
    }

    @Override
    public void deleteOldCaptcha(LocalDateTime time) {
        captchaRepository.deleteOldCaptcha(time);
    }

    @Override
    public CaptchaCode save(CaptchaCode captchaCode) {
        return captchaRepository.save(captchaCode);
    }
}