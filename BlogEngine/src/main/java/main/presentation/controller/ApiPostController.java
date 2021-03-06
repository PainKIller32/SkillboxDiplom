package main.presentation.controller;

import main.domain.service.UserSecurity;
import main.domain.dto.NewPostDto;
import main.domain.model.Post;
import main.domain.usecase.PostUseCase;
import main.presentation.dto.*;
import main.presentation.exception.FalseResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ApiPostController {

    private final HttpSession httpSession;
    private final UserSecurity userSecurity;
    private final PostUseCase postUseCase;

    @Autowired
    public ApiPostController(HttpSession httpSession, UserSecurity userSecurity, PostUseCase postUseCase) {
        this.httpSession = httpSession;
        this.userSecurity = userSecurity;
        this.postUseCase = postUseCase;
    }

    @GetMapping("/api/post")
    public ResponseEntity<PostsDto> getPosts(@RequestParam("offset") int offset,
                                             @RequestParam("limit") int limit,
                                             @RequestParam("mode") String mode) {
        Iterable<Post> posts = postUseCase.getPosts(offset, limit, mode);
        if (posts == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<PostShortViewDto> postDtoList = new ArrayList<>();
        for (Post post : posts) {
            postDtoList.add(new PostShortViewDto(post));
        }

        return new ResponseEntity<>(new PostsDto<>(postUseCase.getPostCount(), postDtoList), HttpStatus.OK);
    }

    @GetMapping("/api/post/search")
    public ResponseEntity<PostsDto<PostShortViewDto>> searchPost(@RequestParam("offset") int offset,
                                                                 @RequestParam("limit") int limit,
                                                                 @RequestParam("query") String query) {
        return getResponseEntityWithPosts(postUseCase.searchPost(query), offset, limit);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity<PostFullViewDto> getPostById(@PathVariable int id) {
        return postUseCase.getPostById(id).map(value ->
                new ResponseEntity<>(new PostFullViewDto(value), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/api/post/byDate")
    public ResponseEntity<PostsDto<PostShortViewDto>> getPostsByDate(@RequestParam("offset") int offset,
                                                                     @RequestParam("limit") int limit,
                                                                     @RequestParam("date") String date) {
        return getResponseEntityWithPosts(postUseCase.getPostsByDate(date), offset, limit);
    }

    @GetMapping("/api/post/byTag")
    public ResponseEntity<PostsDto<PostShortViewDto>> getPostsByTag(@RequestParam("offset") int offset,
                                                                    @RequestParam("limit") int limit,
                                                                    @RequestParam("tag") String tag) {
        return getResponseEntityWithPosts(postUseCase.getPostsByTag(tag), offset, limit);
    }

    @GetMapping("/api/post/moderation")
    public ResponseEntity<PostsDto> getPostByModeration(@RequestParam("offset") int offset,
                                                        @RequestParam("limit") int limit,
                                                        @RequestParam("status") String status) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Post> posts = postUseCase.getPostByModeration(userSecurity.getAuthorizedUserId(httpSession.getId()), offset, limit, status);
        List<PostByModerationDto> postDtoList = new ArrayList<>();
        for (Post post : posts) {
            postDtoList.add(new PostByModerationDto(post));
        }
        return new ResponseEntity<>(new PostsDto<>(posts.size(), postDtoList), HttpStatus.OK);
    }

    @GetMapping("/api/post/my")
    public ResponseEntity<PostsDto<PostShortViewDto>> getMyPosts(@RequestParam("offset") int offset,
                                                                 @RequestParam("limit") int limit,
                                                                 @RequestParam("status") String status) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return getResponseEntityWithPosts(postUseCase.getMyPost(userSecurity.getAuthorizedUserId(httpSession.getId()), offset, limit, status), offset, limit);
    }

    @PostMapping("/api/post")
    public ResponseEntity<ResultDto> addPost(@RequestBody NewPostDto newPost) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        HashMap<String, String> errors = postUseCase.checkPostErrors(newPost.getText(), newPost.getTitle());
        if (!errors.isEmpty()) {
            throw new FalseResultException(errors);
        }
        postUseCase.addPost(userSecurity.getAuthorizedUserId(httpSession.getId()), newPost);
        return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
    }

    @PutMapping("/api/post/{id}")
    public ResponseEntity<ResultDto> editPost(@PathVariable int id, @RequestBody NewPostDto newPost) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        HashMap<String, String> errors = postUseCase.checkPostErrors(newPost.getText(), newPost.getTitle());
        if (!errors.isEmpty()) {
            throw new FalseResultException(errors);
        } else {
            return postUseCase.editPost(userSecurity.getAuthorizedUserId(httpSession.getId()), id, newPost) ?
                    new ResponseEntity<>(ResultDto.success(), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/post/like")
    public ResponseEntity<ResultDto> likePost(@RequestBody PostVoteDto postVote) {
        return votePost(postVote.getPostId(), 1);
    }

    @PostMapping("/api/post/dislike")
    public ResponseEntity<ResultDto> dislikePost(@RequestBody PostVoteDto postVote) {
        return votePost(postVote.getPostId(), -1);
    }


    private ResponseEntity<ResultDto> votePost(int postId, int value) {
        if (!userSecurity.checkUserAuthorization(httpSession.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (postUseCase.votePost(userSecurity.getAuthorizedUserId(httpSession.getId()), postId, value)) {
            return new ResponseEntity<>(ResultDto.success(), HttpStatus.OK);
        } else {
            throw new FalseResultException();
        }
    }

    private ResponseEntity<PostsDto<PostShortViewDto>> getResponseEntityWithPosts(List<Post> posts, int offset, int limit) {
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