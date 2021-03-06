package main.presentation.controller;

import main.domain.dto.EditProfileDto;
import main.domain.dto.NewCommentDto;
import main.domain.dto.SettingsDto;
import main.domain.model.Tag;
import main.domain.service.FileService;
import main.domain.service.UserSecurity;
import main.domain.usecase.*;
import main.presentation.dto.*;
import main.presentation.exception.BadRequestException;
import main.presentation.exception.FalseResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
public class ApiGeneralController {
    private final HttpSession httpSession;
    private final UserSecurity userSecurity;
    private final UserUseCase userUseCase;
    private final PostUseCase postUseCase;
    private final FileService fileService;
    private final CommentUseCase commentUseCase;
    private final TagUseCase tagUseCase;
    private final GlobalSettingUseCase settingUseCase;

    public ApiGeneralController(HttpSession httpSession,
                                UserSecurity userSecurity,
                                UserUseCase userUseCase,
                                FileService fileService,
                                PostUseCase postUseCase,
                                CommentUseCase commentUseCase,
                                TagUseCase tagUseCase,
                                GlobalSettingUseCase settingUseCase) {
        this.httpSession = httpSession;
        this.userSecurity = userSecurity;
        this.userUseCase = userUseCase;
        this.fileService = fileService;
        this.postUseCase = postUseCase;
        this.commentUseCase = commentUseCase;
        this.tagUseCase = tagUseCase;
        this.settingUseCase = settingUseCase;
    }

    @GetMapping("/api/init")
    public ResponseEntity<InitDto> getInit() {
        return new ResponseEntity<>(new InitDto(), HttpStatus.OK);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultDto> editProfileWithPhoto(@RequestParam String name,
                                                          @RequestParam String email,
                                                          @RequestParam(required = false) String password,
                                                          @RequestParam Integer removePhoto,
                                                          @RequestParam MultipartFile photo) {
        EditProfileDto profile = new EditProfileDto();
        profile.setName(name);
        profile.setEmail(email);
        profile.setPassword(password);
        profile.setRemovePhoto(removePhoto);
        profile.setPhoto(photo);
        return editProfile(profile);
    }

    @PostMapping(value = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultDto> editProfile(@RequestBody EditProfileDto profile) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        HashMap<String, String> errors = userUseCase.editProfile(profile, httpSession.getId());
        if (errors == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (errors.isEmpty()) {
            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            throw new FalseResultException(errors);
        }
    }

    @PostMapping("/api/image")
    public String uploadImage(@RequestParam("image") MultipartFile file) {
        return fileService.uploadImage(file);
    }

    @PostMapping("/api/comment")
    public ResponseEntity<ResultDto> addComment(@RequestBody NewCommentDto comment) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (commentUseCase.checkExistence(comment)) {
            throw new BadRequestException("Комментарий или пост не существует");
        }
        if (commentUseCase.checkTextErrors(comment.getText())) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("text", "Текст комментария не задан или слишком короткий");
            throw new FalseResultException(errors);
        }
        int id = commentUseCase.addComment(comment, httpSession.getId());
        return new ResponseEntity<>(new ResultWithIdDto(id), HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagsDto> getTags(@RequestParam(value = "query", defaultValue = "") String query) {
        HashMap<Tag, Double> tags = tagUseCase.getTagsWithWeight(query);
        if (tags.isEmpty()) {
            return new ResponseEntity<>(new TagsDto(new ArrayList<>()), HttpStatus.OK);
        }
        List<TagDto> tagDtoList = new ArrayList<>();
        for (Tag tag : tags.keySet()) {
            tagDtoList.add(new TagDto(tag.getName(), tags.get(tag)));
        }
        return new ResponseEntity<>(new TagsDto(tagDtoList), HttpStatus.OK);
    }

    @PostMapping("/api/moderation")
    public ResponseEntity<HttpStatus> setModerationStatus(@RequestBody ModerationDto moderation) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (postUseCase.setModerationStatus(
                moderation.getPostId(),
                userSecurity.getAuthorizedUserId(httpSession.getId()),
                moderation.getDecision())) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BadRequestException("Пост не существует");
        }
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<PostByYearCountDto> getPostsCountByYear(@RequestParam("year") String year) {
        PostByYearCountDto posts = new PostByYearCountDto(postUseCase.getPostsByYearCount(year), postUseCase.getYears());
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/api/settings")
    public ResponseEntity<LinkedHashMap<String, Boolean>> getSettings() {
        return new ResponseEntity<>(settingUseCase.getSettings(), HttpStatus.OK);
    }

    @PutMapping("/api/settings")
    public void saveSettings(@RequestBody SettingsDto settings) {
        settingUseCase.saveSettings(settings, userSecurity.getAuthorizedUserId(httpSession.getId()));
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<LinkedHashMap<String, Object>> getAllStatistics() {
        if (!settingUseCase.isStatisticsPublic()) {
            if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(postUseCase.getAllStatistics(), HttpStatus.OK);
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity<LinkedHashMap<String, Object>> getMyStatistics() {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(postUseCase.getStatisticsByUser(userSecurity.getAuthorizedUserId(httpSession.getId())), HttpStatus.OK);
    }
}