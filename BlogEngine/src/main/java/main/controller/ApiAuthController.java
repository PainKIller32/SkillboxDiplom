package main.controller;

import main.UserSecurity;
import main.dto.*;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@RestController
public class ApiAuthController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaCodeRepository captchaRepository;
    private final HttpSession httpSession;
    private final UserSecurity userSecurity;
    private final JavaMailSenderImpl mailSender;

    @Autowired
    public ApiAuthController(UserRepository userRepository,
                             PostRepository postRepository,
                             CaptchaCodeRepository captchaRepository,
                             HttpSession httpSession,
                             JavaMailSenderImpl mailSender,
                             UserSecurity userSecurity) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.captchaRepository = captchaRepository;
        this.httpSession = httpSession;
        this.mailSender = mailSender;
        this.userSecurity = userSecurity;
    }

    @Value("${domain}")
    String domain;

    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLogin) {
        Optional<User> findUser = userRepository.findByEmailEqualsAndPasswordEquals(userLogin.getE_mail(), userLogin.getPassword());
        if (findUser.isPresent()) {
            userSecurity.authorizeUser(httpSession.getId(), findUser.get().getId());
            return new ResponseEntity<>(new ResultWithUserDto(true, getUserByAuth(findUser.get())), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK);
        }
    }

    @GetMapping("/api/auth/check")
    public ResponseEntity checkUser() {
        ResponseEntity responseEntity = new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK);
        if (userSecurity.checkUserAuthorization(httpSession.getId())) {
            Optional<User> findUser = userRepository.findById(userSecurity.getAuthorizedUserId(httpSession.getId()));
            if (findUser.isPresent()) {
                responseEntity = new ResponseEntity<>(new ResultWithUserDto(true, getUserByAuth(findUser.get())), HttpStatus.OK);
            }
        }
        return responseEntity;
    }

    @PostMapping("/api/auth/restore")
    public ResponseEntity restorePassword(@RequestBody UserEmailDto email) {
        Optional<User> findUser = userRepository.findByEmailEquals(email.getEmail());
        if (findUser.isPresent()) {
            User user = findUser.get();
            MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
                String hash = RandomStringUtils.randomAlphanumeric(45);
                String messageText = "<a href= \" " + domain + "/login/change-password/" + hash + "\">" + domain + "/login/change-password/\"" + hash + "</a>";
                message.setContent(messageText, "text/html");
                helper.setTo(user.getEmail());
                message.setFrom("devpub@mail.ru");
                message.setSubject("Восстановление пароля");
                mailSender.send(message);
                user.setCode(hash);
                userRepository.save(user);

            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK);
        }
    }

    @PostMapping("/api/auth/password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePassword) {
        HashMap<String, String> errors = new HashMap<>();
        Optional<User> user = userRepository.findByCodeEquals(changePassword.getCode());
        if (user.isEmpty()) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        } else {
            Optional<CaptchaCode> captchaCode = captchaRepository.findBySecretCodeEquals(changePassword.getCaptcha_secret());
            if (captchaCode.isPresent()) {
                if (!changePassword.getCaptcha().equals(captchaCode.get().getCode())) {
                    errors.put("captcha", "Код с картинки введён неверно");
                }
            } else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            if (changePassword.getPassword().length() < 6) {
                errors.put("password", "Пароль короче 6-ти символов");
            } else {
                user.get().setPassword(changePassword.getPassword());
                userRepository.save(user.get());
            }
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
        }
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity registerUser(@RequestBody UserRegisterDto register) {
        HashMap<String, String> errors = new HashMap<>();
        Optional<User> userByEmail = userRepository.findByEmailEquals(register.getEmail());
        if (userByEmail.isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (register.getPassword().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        Optional<CaptchaCode> captchaCode = captchaRepository.findBySecretCodeEquals(register.getCaptchaSecret());
        if (captchaCode.isPresent()) {
            if (!register.getCaptcha().equals(captchaCode.get().getCode())) {
                errors.put("captcha", "Код с картинки введён неверно");
            }
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (errors.isEmpty()) {
            User user = new User();
            //user.setName(name);
            user.setEmail(register.getEmail());
            user.setPassword(register.getPassword());
            user.setModerator(false);
            user.setRegTime(LocalDateTime.now());
            userRepository.save(user);
            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
        }
    }

    @Value("${captchaLifeTime}")
    Long captchaLifeTime;

    @GetMapping("/api/auth/captcha")
    public ResponseEntity getCaptcha() {
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(RandomStringUtils.randomNumeric(5));
        captchaCode.setSecretCode(RandomStringUtils.randomAlphanumeric(22));
        captchaCode.setTime(LocalDateTime.now());
        captchaRepository.deleteOldCaptcha(LocalDateTime.now().minusMinutes(captchaLifeTime));
        captchaRepository.save(captchaCode);
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
        return new ResponseEntity<>(new CaptchaDto(captchaCode.getSecretCode(), image), HttpStatus.OK);
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity logOut() {
        userSecurity.removeUserAuthorization(httpSession.getId());
        return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
    }

    private int getRandomInt(int max) {
        return new Random().nextInt(max);
    }

    private UserByAuthDto getUserByAuth(User user) {
        int moderationCount = 0;
        if (user.isModerator()) {
            moderationCount = postRepository.getModerationPostCount(user.getId());
        }
        return new UserByAuthDto(user, moderationCount);
    }
}