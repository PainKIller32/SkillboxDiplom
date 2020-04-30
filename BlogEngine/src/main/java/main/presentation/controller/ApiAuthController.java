package main.presentation.controller;

import main.domain.dto.CaptchaDto;
import main.domain.dto.ChangePasswordDto;
import main.domain.dto.UserRegisterDto;
import main.domain.model.User;
import main.domain.usecase.CaptchaUseCase;
import main.domain.usecase.UserUseCase;
import main.presentation.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class ApiAuthController {
    private final HttpSession httpSession;
    private final UserUseCase userUseCase;
    private final CaptchaUseCase captchaUseCase;

    @Autowired
    public ApiAuthController(HttpSession httpSession, UserUseCase userUseCase, CaptchaUseCase captchaUseCase) {
        this.httpSession = httpSession;
        this.userUseCase = userUseCase;
        this.captchaUseCase = captchaUseCase;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<ResultDto> login(@RequestBody UserLoginDto userLogin) {
        Optional<User> findUser = userUseCase.login(userLogin.getEmail(), userLogin.getPassword(), httpSession.getId());
        return findUser.<ResponseEntity<ResultDto>>map(
                user -> new ResponseEntity<>(new ResultWithUserDto(true, new UserByAuthDto(user, userUseCase.getModerationCount(user))), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK));
    }

    @GetMapping("/api/auth/check")
    public ResponseEntity<ResultDto> checkUser() {
        User user = userUseCase.check(httpSession.getId());
        if (user != null) {
            return new ResponseEntity<>(new ResultWithUserDto(true, new UserByAuthDto(user, userUseCase.getModerationCount(user))), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK);
    }

    @PostMapping("/api/auth/restore")
    public ResponseEntity<ResultDto> restorePassword(@RequestBody UserEmailDto email) {
        return new ResponseEntity<>(userUseCase.restorePassword(email.getEmail()) ? ResultDto.success() : ResultDto.decline(), HttpStatus.OK);
    }

    @PostMapping("/api/auth/password")
    public ResponseEntity<ResultDto> changePassword(@RequestBody ChangePasswordDto changePassword) {
        HashMap<String, String> errors = userUseCase.changePassword(changePassword);
        if (errors.isEmpty()) {
            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
        }
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<ResultDto> registerUser(@RequestBody UserRegisterDto register) {
        HashMap<String, String> errors = userUseCase.register(register);
        if (errors.isEmpty()) {
            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
        }
    }

    @GetMapping("/api/auth/captcha")
    public ResponseEntity<CaptchaDto> getCaptcha() {
        return new ResponseEntity<>(captchaUseCase.generateCaptcha(), HttpStatus.OK);
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity<ResultDto> logOut() {
        userUseCase.logOut(httpSession.getId());
        return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
    }
}