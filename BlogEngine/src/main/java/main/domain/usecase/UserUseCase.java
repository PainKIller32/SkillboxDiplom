package main.domain.usecase;

import main.domain.dto.ChangePasswordDto;
import main.domain.dto.EditProfileDto;
import main.domain.dto.UserRegisterDto;
import main.domain.model.CaptchaCode;
import main.domain.model.User;
import main.domain.port.CaptchaRepositoryPort;
import main.domain.port.PostRepositoryPort;
import main.domain.port.UserRepositoryPort;
import main.domain.service.FileService;
import main.domain.service.UserSecurity;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Component
@Transactional
public class UserUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final UserSecurity userSecurity;
    private final PostRepositoryPort postRepositoryPort;
    private final JavaMailSenderImpl mailSender;
    private final CaptchaRepositoryPort captchaRepositoryPort;
    private final FileService fileService;

    public UserUseCase(UserRepositoryPort userRepositoryPort,
                       UserSecurity userSecurity,
                       PostRepositoryPort postRepositoryPort,
                       JavaMailSenderImpl mailSender,
                       CaptchaRepositoryPort captchaRepositoryPort,
                       FileService fileService) {
        this.userRepositoryPort = userRepositoryPort;
        this.userSecurity = userSecurity;
        this.postRepositoryPort = postRepositoryPort;
        this.mailSender = mailSender;
        this.captchaRepositoryPort = captchaRepositoryPort;
        this.fileService = fileService;
    }

    public Optional<User> login(String email, String password, String sessionId) {
        Optional<User> user = userRepositoryPort.findByEmailAndPassword(email, password);
        user.ifPresent(value -> userSecurity.authorizeUser(sessionId, value.getId()));
        return user;
    }

    public void logOut(String sessionId) {
        userSecurity.removeUserAuthorization(sessionId);
    }

    public User check(String sessionId) {
        if (userSecurity.checkUserAuthorization(sessionId)) {
            return userRepositoryPort.findById(userSecurity.getAuthorizedUserId(sessionId)).orElseThrow();
        } else {
            return null;
        }
    }

    public int getModerationCount(User user) {
        return postRepositoryPort.countByModeratorId(user.getId());
    }

    @Value("${domain}")
    String domain;

    public boolean restorePassword(String email) {
        Optional<User> findUser = userRepositoryPort.findByEmail(email);
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
                userRepositoryPort.save(user);
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public HashMap<String, String> changePassword(ChangePasswordDto changePassword) {
        HashMap<String, String> errors = new HashMap<>();
        Optional<User> user = userRepositoryPort.findByCode(changePassword.getCode());
        if (user.isEmpty()) {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        } else {
            if (checkCaptcha(changePassword.getCaptcha(), changePassword.getCaptchaSecret())) {
                errors.put("captcha", "Код с картинки введён неверно");
            }
            if (checkPassword(changePassword.getPassword())) {
                errors.put("password", "Пароль короче 6-ти символов");
            }
            if (errors.isEmpty()) {
                user.get().setPassword(changePassword.getPassword());
                userRepositoryPort.save(user.get());
            }
        }
        return errors;
    }

    public HashMap<String, String> register(UserRegisterDto register) {
        HashMap<String, String> errors = new HashMap<>();
        Optional<User> userByEmail = userRepositoryPort.findByEmail(register.getEmail());
        if (userByEmail.isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (checkPassword(register.getPassword())) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (checkCaptcha(register.getCaptcha(), register.getCaptchaSecret())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.isEmpty()) {
            User user = new User();
            user.setName(register.getName());
            user.setEmail(register.getEmail());
            user.setPassword(register.getPassword());
            user.setModerator(false);
            user.setRegTime(LocalDateTime.now());
            userRepositoryPort.save(user);
        }
        return errors;
    }

    public HashMap<String, String> editProfile(EditProfileDto profile, String sessionId) {
        Optional<User> findUser = userRepositoryPort.findById(userSecurity.getAuthorizedUserId(sessionId));
        HashMap<String, String> errors = new HashMap<>();
        if (findUser.isPresent()) {
            User user = findUser.get();
            String password = profile.getPassword();
            if (password != null) {
                if (checkPassword(password)) {
                    errors.put("password", "Пароль короче 6-ти символов");
                }
            } else {
                password = user.getPassword();
            }
            String name = profile.getName();
            if (!name.equals(user.getName())) {
                Optional<User> userByName = userRepositoryPort.findByName(name);
                if (userByName.isPresent()) {
                    errors.put("name", "Имя указано неверно");
                }
            }
            String email = profile.getEmail();
            if (!email.equals(user.getEmail())) {
                Optional<User> userByEmail = userRepositoryPort.findByEmail(email);
                if (userByEmail.isPresent()) {
                    errors.put("email", "Этот e-mail уже зарегистрирован");
                }
            }

            if (errors.isEmpty()) {
                MultipartFile photo = profile.getPhoto();
                if (photo != null && !photo.isEmpty()) {
                    if (photo.getSize() > 5000000) {
                        errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
                        return errors;
                    } else {
                        if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                            fileService.deleteFile(new File(user.getPhoto().substring(1, 10)));
                        }
                        String uploadPhoto = fileService.uploadImage(photo);
                        if (uploadPhoto != null) {
                            user.setPhoto(uploadPhoto);
                        }
                    }
                }

                Integer removePhoto = profile.getRemovePhoto();
                if (removePhoto != null && removePhoto == 1) {
                    if (fileService.deleteFile(new File(user.getPhoto().substring(1, 10)))) {
                        user.setPhoto(null);
                    }
                }
                user.setName(name);
                user.setEmail(email);
                user.setPassword(password);
                userRepositoryPort.save(user);
            }
        } else {
            errors = null;
        }
        return errors;
    }

    private boolean checkPassword(String password) {
        return password.length() < 6;
    }

    private boolean checkCaptcha(String captcha, String captchaSecret) {
        Optional<CaptchaCode> captchaCode = captchaRepositoryPort.findBySecretCode(captchaSecret);
        return captchaCode.filter(code -> captcha.equals(code.getCode())).isEmpty();
    }
}