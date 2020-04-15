package main.controller;

import main.UserSecurity;
import main.dto.*;
import main.model.*;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
public class ApiPostController {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentRepository postCommentRepository;
    private final HttpSession httpSession;
    private final UserSecurity userSecurity;
    private final UserRepository userRepository;

    @Autowired
    public ApiPostController(PostRepository postRepository,
                             PostVoteRepository postVoteRepository,
                             PostCommentRepository postCommentRepository,
                             HttpSession httpSession,
                             UserSecurity userSecurity,
                             UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentRepository = postCommentRepository;
        this.httpSession = httpSession;
        this.userSecurity = userSecurity;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/post")
    public ResponseEntity getPost(@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("mode") String mode) {
        Iterable<Post> posts = null;
        List<Integer> postsId = new ArrayList<>();
        switch (mode) {
            case "recent":
                posts = postRepository.findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqual(
                        1,
                        ModerationStatus.ACCEPTED,
                        LocalDateTime.now(),
                        PageRequest.of(offset / limit, limit, Sort.by("time").descending()));
                break;
            case "popular":
                List<CommentCount> commentCounts = postCommentRepository.getPopularPostId(PageRequest.of(offset / limit, limit));
                for (CommentCount commentCount : commentCounts) {
                    postsId.add(commentCount.getPostId());
                }
                posts = postRepository.findAllById(postsId);
                break;
            case "best":
                List<LikeCount> likeCounts = postVoteRepository.getBestPostsId(PageRequest.of(offset / limit, limit));
                for (LikeCount likeCount : likeCounts) {
                    postsId.add(likeCount.getPostId());
                }
                posts = postRepository.findAllById(postsId);
                break;
            case "early":
                posts = postRepository.findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqual(
                        1,
                        ModerationStatus.ACCEPTED,
                        LocalDateTime.now(),
                        PageRequest.of(offset / limit, limit, Sort.by("time")));
                break;
        }

        if (posts == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        List<PostShortViewDto> postDtoList = new ArrayList<>();
        for (Post post : posts) {
            postDtoList.add(new PostShortViewDto(post));
        }

        return new ResponseEntity<>(new PostsDto<>(postRepository.getPostCount(), postDtoList), HttpStatus.OK);
    }

    @GetMapping("/api/post/search")
    public ResponseEntity searchPost(@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("query") String query) {
        List<Post> posts;
        if (query.isEmpty()) {
            posts = postRepository.findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqual(
                    1,
                    ModerationStatus.ACCEPTED,
                    LocalDateTime.now(),
                    null
            );
        } else {
            posts = postRepository.findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqualAndTextContains(
                    1,
                    ModerationStatus.ACCEPTED,
                    LocalDateTime.now(),
                    query
            );
        }
        return getResponseEntityWithPosts(posts, offset, limit);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity getPostById(@PathVariable int id) {
        Optional<Post> post = postRepository.findPostByIdAndIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqual(
                id,
                1,
                ModerationStatus.ACCEPTED,
                LocalDateTime.now()
        );
        return post.map(value -> new ResponseEntity<>(new PostFullViewDto(value), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/post/byDate")
    public ResponseEntity getPostsByDate(@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("date") String date) {
        LocalDateTime localDateTime = LocalDate.parse(date).atStartOfDay();
        List<Post> posts = postRepository.findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeBetween(
                1,
                ModerationStatus.ACCEPTED,
                localDateTime,
                localDateTime.plusDays(1).minusSeconds(1)
        );
        return getResponseEntityWithPosts(posts, offset, limit);
    }

    @GetMapping("/api/post/byTag")
    public ResponseEntity getPostsByTag(@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("tag") String tag) {
        List<Post> posts = postRepository.getPostByTag(tag);
        return getResponseEntityWithPosts(posts, offset, limit);
    }

    @GetMapping("/api/post/moderation")
    public ResponseEntity getPostByModeration(@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("status") String status) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Optional<User> findUser = userRepository.findById(userSecurity.getAuthorizedUserId(httpSession.getId()));
        if (findUser.isPresent()) {
            User user = findUser.get();
            if (user.isModerator()) {
                List<Post> posts;
                if (status.equals("new")) {
                    posts = postRepository.findPostsByIsActiveEqualsAndModerationStatusEquals(
                            1,
                            ModerationStatus.NEW,
                            PageRequest.of(offset / limit, limit)
                    );
                } else {
                    posts = postRepository.findPostsByIsActiveEqualsAndModeratorIdEqualsAndModerationStatusEquals(
                            1,
                            user.getId(),
                            ModerationStatus.valueOf(status.toUpperCase()),
                            PageRequest.of(offset / limit, limit)
                    );
                }

                List<PostByModerationDto> postDtoList = new ArrayList<>();
                for (Post post : posts) {
                    postDtoList.add(new PostByModerationDto(post));
                }
                return new ResponseEntity<>(new PostsDto<>(posts.size(), postDtoList), HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/post/my")
    public ResponseEntity getMyPosts(@RequestParam("offset") int offset, @RequestParam("limit") int limit, @RequestParam("status") String status) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        int userId = userSecurity.getAuthorizedUserId(httpSession.getId());
        List<Post> posts = new ArrayList<>();
        switch (status) {
            case "inactive":
                posts = postRepository.findPostsByIdAndIsActiveEquals(userId, 0, PageRequest.of(offset / limit, limit));
                break;
            case "pending":
                posts = postRepository.findPostsByUserIdEqualsAndIsActiveEqualsAndModerationStatusEquals(
                        userId,
                        1,
                        ModerationStatus.NEW,
                        PageRequest.of(offset / limit, limit));
                break;
            case "declined":
                posts = postRepository.findPostsByUserIdEqualsAndIsActiveEqualsAndModerationStatusEquals(
                        userId,
                        1,
                        ModerationStatus.DECLINED,
                        PageRequest.of(offset / limit, limit));
                break;
            case "published":
                posts = postRepository.findPostsByUserIdEqualsAndIsActiveEqualsAndModerationStatusEquals(
                        userId,
                        1,
                        ModerationStatus.ACCEPTED,
                        PageRequest.of(offset / limit, limit));
                break;
        }
        return getResponseEntityWithPosts(posts, offset, limit);
    }

    @PostMapping("/api/post")
    public ResponseEntity addPost(@RequestBody NewPostDto newPost) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        HashMap<String, String> errors = checkPostErrors(newPost.getText(), newPost.getTitle());
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
        }
        Post post = new Post();
        post.setActive(newPost.getActive());
        post.setText(newPost.getText());
        post.setTitle(newPost.getTitle());
        post.setTime(LocalDateTime.parse(newPost.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        post.setModerationStatus(ModerationStatus.NEW);
        post.setUserId(userSecurity.getAuthorizedUserId(httpSession.getId()));
        post.setTags(getTagList(newPost.getTags()));
        postRepository.save(post);
        return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
    }

    @PutMapping("/api/post/{id}")
    public ResponseEntity editPost(@PathVariable int id, @RequestBody NewPostDto newPost) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Optional<Post> findPost = postRepository.findById(id);
        if (findPost.isPresent()) {
            Optional<User> findUser = userRepository.findById(userSecurity.getAuthorizedUserId(httpSession.getId()));
            if (findUser.isPresent()) {
                User user = findUser.get();
                Post post = findPost.get();
                HashMap<String, String> errors = checkPostErrors(newPost.getText(), newPost.getTitle());
                if (!errors.isEmpty()) {
                    return new ResponseEntity<>(new ResultWithErrorsDto(false, errors), HttpStatus.OK);
                }
                if (!user.isModerator()) {
                    post.setModerationStatus(ModerationStatus.NEW);
                }
                post.setActive(newPost.getActive());
                post.setText(newPost.getText());
                post.setTitle(newPost.getTitle());
                post.setTime(LocalDateTime.parse(newPost.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                post.setTags(getTagList(newPost.getTags()));
                postRepository.save(post);
                return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/post/like")
    public ResponseEntity likePost(@RequestBody PostVoteDto postVote) {
        return votePost(postVote.getPost_id(), 1);
    }

    @PostMapping("/api/post/dislike")
    public ResponseEntity dislikePost(@RequestBody PostVoteDto postVote) {
        return votePost(postVote.getPost_id(), -1);
    }


    private ResponseEntity votePost(int postId, int value) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Optional<PostVote> postVote = postVoteRepository.findByUserIdEqualsAndPostIdEquals(userSecurity.getAuthorizedUserId(httpSession.getId()), postId);
        if (postVote.isPresent()) {
            if (postVote.get().getValue() == value) {
                return new ResponseEntity<>(ResultDto.decline(), HttpStatus.OK);
            } else {
                postVoteRepository.delete(postVote.get());
            }
        }
        PostVote newPostVote = new PostVote();
        newPostVote.setPostId(postId);
        newPostVote.setUserId(userSecurity.getAuthorizedUserId(httpSession.getId()));
        newPostVote.setTime(LocalDateTime.now());
        newPostVote.setValue(value);
        postVoteRepository.save(newPostVote);
        return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
    }

    private List<Tag> getTagList(String[] tags) {
        List<Tag> tagsList = new ArrayList<>();
        for (String tag : tags) {
            tagsList.add(new Tag(tag));
        }
        return tagsList;
    }

    private HashMap<String, String> checkPostErrors(String text, String title) {
        HashMap<String, String> errors = new HashMap<>();
        if (title.isEmpty() || title.length() < 10) {
            errors.put("title", "Заголовок не установлен");
        }
        if (text.isEmpty() || text.length() < 500) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        return errors;
    }

    private ResponseEntity getResponseEntityWithPosts(List<Post> posts, int offset, int limit) {
        if (posts.isEmpty()) {
            return new ResponseEntity<>(new PostsDto<>(posts.size(), new ArrayList<>()), HttpStatus.OK);
        }

        List<PostShortViewDto> postDtoList = new ArrayList<>();
        for (int i = offset; i < (limit + offset) && i < posts.size(); i++) {
            postDtoList.add(new PostShortViewDto(posts.get(i)));
        }
        return new ResponseEntity<>(new PostsDto<>(posts.size(), postDtoList), HttpStatus.OK);
    }
}