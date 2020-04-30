package main.domain.usecase;

import main.domain.dto.CaptchaDto;
import main.domain.model.CaptchaCode;
import main.domain.port.CaptchaRepositoryPort;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Component
@Transactional(readOnly = true)
public class CaptchaUseCase {
    private final CaptchaRepositoryPort captchaRepositoryPort;

    public CaptchaUseCase(CaptchaRepositoryPort captchaRepositoryPort) {
        this.captchaRepositoryPort = captchaRepositoryPort;
    }

    @Value("${captchaLifeTime}")
    Long captchaLifeTime;

    public CaptchaDto generateCaptcha() {
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(RandomStringUtils.randomNumeric(5));
        captchaCode.setSecretCode(RandomStringUtils.randomAlphanumeric(22));
        captchaCode.setTime(LocalDateTime.now());
        captchaRepositoryPort.deleteOldCaptcha(LocalDateTime.now().minusMinutes(captchaLifeTime));
        captchaRepositoryPort.save(captchaCode);
        String image = null;
        try {
            Font font = Font.createFont(Font.PLAIN, getClass().getResourceAsStream("/static/fonts/OutlineFont.ttf")).deriveFont(23f);
            BufferedImage bufferedImage = new BufferedImage(84, 43, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setBackground(Color.WHITE);
            g2.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            g2.setFont(font);
            g2.setColor(Color.gray);
            g2.drawString(captchaCode.getCode(), 2, 30);
            for (int i = 0; i < 4; i++) {
                g2.drawLine(getRandomInt(84), getRandomInt(43), getRandomInt(84), getRandomInt(43));
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", baos);
            image = "data:image/png;base64, " + Base64.getEncoder().encodeToString(baos.toByteArray());
            baos.close();
            baos.flush();
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return new CaptchaDto(captchaCode.getSecretCode(), image);
    }

    private int getRandomInt(int max) {
        return new Random().nextInt(max);
    }
}