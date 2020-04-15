package main.controller;

import main.UserSecurity;
import main.dto.*;
import main.model.*;
import main.repository.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class ApiGeneralController {
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final HttpSession httpSession;
    private final TagRepository tagRepository;
    private final GlobalSettingRepository settingRepository;
    private final PostVoteRepository voteRepository;
    private final UserRepository userRepository;
    private final UserSecurity userSecurity;

    public ApiGeneralController(PostRepository postRepository,
                                PostCommentRepository commentRepository,
                                HttpSession httpSession,
                                TagRepository tagRepository,
                                GlobalSettingRepository settingRepository,
                                PostVoteRepository voteRepository,
                                UserRepository userRepository,
                                UserSecurity userSecurity) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.httpSession = httpSession;
        this.tagRepository = tagRepository;
        this.settingRepository = settingRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.userSecurity = userSecurity;
    }

    @Value("${downloadImagePath}")
    String downloadImagePath;

    @GetMapping("/api/init")
    public ResponseEntity getInit() {
        return new ResponseEntity<>(new InitDto(), HttpStatus.OK);
    }

    @PostMapping("/api/profile/my")
    public ResponseEntity editProfile(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam(required = false) String password,
                                      @RequestParam Integer removePhoto,
                                      @RequestParam MultipartFile photo) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> findUser = userRepository.findById(userSecurity.getAuthorizedUserId(httpSession.getId()));
        if (findUser.isPresent()) {
            User user = findUser.get();
            HashMap<String, String> errors = new HashMap<>();
            if (password != null) {
                if (password.length() < 6) {
                    errors.put("password", "Пароль короче 6-ти символов");
                } else {
                    user.setPassword(password);
                }
            }
            if (!name.equals(user.getName())) {
                Optional<User> userByName = userRepository.findByNameEquals(name);
                if (userByName.isPresent()) {
                    errors.put("name", "Имя указано неверно");
                } else {
                    user.setName(name);
                }
            }
            if (!email.equals(user.getEmail())) {
                Optional<User> userByEmail = userRepository.findByEmailEquals(email);
                if (userByEmail.isPresent()) {
                    errors.put("email", "Этот e-mail уже зарегистрирован");
                } else {
                    user.setEmail(email);
                }
            }
            if (photo != null && !photo.isEmpty()) {
                if (photo.getSize() > 5000000) {
                    errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
                } else {
                    deleteFile(new File(user.getPhoto().substring(1, 10)));
                    user.setPhoto(uploadImage(photo));
                }
            }
            if (errors.isEmpty()) {
                if (removePhoto != null && removePhoto == 1) {
                    if (deleteFile(new File(user.getPhoto().substring(1, 10)))) {
                        user.setPhoto(null);
                    }
                }
                userRepository.save(user);
                return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean deleteFile(File file) {
        boolean result;
        if (file.isDirectory()) {
            String[] files = file.list();
            if (files == null || files.length == 0) {
                result = file.delete();
            } else {
                for (String fileName : files) {
                    deleteFile(new File(file.getPath() + File.separator + fileName));
                }
                result = file.delete();
            }
        } else {
            result = file.delete();
        }
        return result;
    }

    @PostMapping("/api/image")
    public String uploadImage(@RequestParam("image") MultipartFile file) {
        String savePathHash = RandomStringUtils.randomAlphabetic(6);
        String savePath = downloadImagePath +
                savePathHash.substring(0, 2) +
                "/" +
                savePathHash.substring(2, 4) +
                "/" +
                savePathHash.substring(4, 6) +
                "/";
        File saveFile = new File(savePath);
        if (saveFile.mkdirs()) {
            savePath += RandomStringUtils.randomAlphanumeric(5) + ".jpg";
            saveFile = new File(savePath);
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                stream.write(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "/" + savePath;
    }

    @PostMapping("/api/comment")
    public ResponseEntity addComment(@RequestBody NewCommentDto comment) {
        if (!postRepository.existsById(comment.getPost_id()) || (comment.getParent_id() != null && !commentRepository.existsById(comment.getParent_id()))) {
            HashMap<String, String> message = new HashMap<>();
            message.put("message", "Комментарий или пост не существует");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (comment.getText().isEmpty() || comment.getText().length() < 2) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("text", "Текст комментария не задан или слишком короткий");
            return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
        }
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        PostComment postComment = new PostComment();
        postComment.setText(comment.getText());
        postComment.setPostId(comment.getPost_id());
        postComment.setTime(LocalDateTime.now());
        postComment.setParentId(comment.getParent_id());
        postComment.setUserId(userSecurity.getAuthorizedUserId(httpSession.getId()));
        int id = commentRepository.save(postComment).getId();
        HashMap<String, Integer> commentId = new HashMap<>();
        commentId.put("id", id);
        return new ResponseEntity<>(commentId, HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    public ResponseEntity getTags(@RequestParam(value = "query", defaultValue = "") String query) {
        Iterable<Tag> tags;
        if (query.isEmpty()) {
            tags = tagRepository.findAll();
        } else {
            tags = tagRepository.findTagsByNameIsStartingWith(query.trim());
        }
        if (!tags.iterator().hasNext()) {
            return new ResponseEntity<>(new TagsDto(new ArrayList<>()), HttpStatus.OK);
        }
        TreeSet<Double> frequency = new TreeSet<>();
        List<TagDto> tagDtoList = new ArrayList<>();
        for (Tag tag : tags) {
            frequency.add(tag.getFrequencyOfOccurrence());
        }
        double postCount = postRepository.getViewPostCount();
        double ratio = 1 / (frequency.last() / postCount);
        for (Tag tag : tags) {
            tagDtoList.add(new TagDto(tag.getName(), (tag.getFrequencyOfOccurrence() / postCount) * ratio));// округление до 3 знака? (Math.round(... * 1000) / 1000)
        }
        return new ResponseEntity<>(new TagsDto(tagDtoList), HttpStatus.OK);
    }

    @PostMapping("/api/moderation")
    public ResponseEntity setModerationStatus(@RequestBody ModerationDto moderation) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Optional<Post> findPost = postRepository.findById(moderation.getPost_id());
        if (findPost.isPresent()) {
            Post post = findPost.get();
            switch (moderation.getDecision()) {
                case "accept":
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                    break;
                case "decline":
                    post.setModerationStatus(ModerationStatus.DECLINED);
                    break;
                case "new":
                    post.setModerationStatus(ModerationStatus.NEW);
                    break;
            }
            post.setModeratorId(userSecurity.getAuthorizedUserId(httpSession.getId()));
            postRepository.save(post);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            HashMap<String, String> message = new HashMap<>();
            message.put("message", "Пост не существует");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/calendar")
    public ResponseEntity getPostsCountByYear(@RequestParam("year") String year) {
        String searchYear = year;
        if (year.isEmpty()) {
            searchYear = Integer.toString(LocalDate.now().getYear());
        }
        List<PostByYearCount> list = postRepository.getYearCounts(searchYear);
        LinkedList<Integer> years = postRepository.getYears();
        PostByYearCountDto posts = new PostByYearCountDto(list, years);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/api/settings")
    public ResponseEntity getSettings() {
        Iterable<GlobalSetting> globalSettings = settingRepository.findAll();
        LinkedHashMap<String, Boolean> settings = new LinkedHashMap<>();
        for (GlobalSetting setting : globalSettings) {
            settings.put(setting.getCode(), setting.getValue().equals("YES"));
        }
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    @PutMapping("/api/settings")
    public void saveSettings(@RequestBody SettingsDto settings) {
        Optional<User> findUser = userRepository.findById(userSecurity.getAuthorizedUserId(httpSession.getId()));
        if (findUser.isPresent()) {
            User user = findUser.get();
            if (user.isModerator()) {
                Iterable<GlobalSetting> globalSettings = settingRepository.findAll();
                for (GlobalSetting setting : globalSettings) {
                    switch (setting.getCode()) {
                        case "MULTIUSER_MODE":
                            setting.setValue(settings.getMULTIUSER_MODE() ? "YES" : "NO");
                            break;
                        case "POST_PREMODERATION":
                            setting.setValue(settings.getPOST_PREMODERATION() ? "YES" : "NO");
                            break;
                        case "STATISTICS_IS_PUBLIC":
                            setting.setValue(settings.getSTATISTICS_IS_PUBLIC() ? "YES" : "NO");
                            break;
                    }
                }
                settingRepository.saveAll(globalSettings);
            }
        }
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity getAllStatistics() {
        Optional<GlobalSetting> globalSetting = settingRepository.findByCodeEquals("STATISTICS_IS_PUBLIC");
        if (globalSetting.isPresent() && globalSetting.get().getValue().equals("NO")) {
            if (userSecurity.checkUserAuthorization(httpSession.getId())) {
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
        }
        LinkedHashMap<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("postsCount", postRepository.getPostCount());
        statistics.put("likesCount", voteRepository.getLikeCount());
        statistics.put("dislikesCount", voteRepository.getDislikeCount());
        statistics.put("viewsCount", postRepository.getViewCount());
        statistics.put("firstPublication", postRepository.getFirstPostDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity getMyStatistics() {
        if (userSecurity.checkUserAuthorization(httpSession.getId())) {
            List<Post> posts = postRepository.findAllByUserIdEquals(userSecurity.getAuthorizedUserId(httpSession.getId()));
            int likeCount = 0;
            int dislikeCount = 0;
            int viewCount = 0;
            LocalDateTime date = LocalDateTime.now();
            for (Post post : posts) {
                likeCount += post.getLikeCount();
                dislikeCount += post.getDislikeCount();
                viewCount += post.getViewCount();
                if (post.getTime().isBefore(date)) {
                    date = post.getTime();
                }
            }
            LinkedHashMap<String, Object> statistics = new LinkedHashMap<>();
            statistics.put("postsCount", posts.size());
            statistics.put("likesCount", likeCount);
            statistics.put("dislikesCount", dislikeCount);
            statistics.put("viewsCount", viewCount);
            statistics.put("firstPublication", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}