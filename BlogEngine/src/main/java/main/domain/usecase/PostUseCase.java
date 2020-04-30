package main.domain.usecase;

import main.domain.dto.NewPostDto;
import main.domain.model.*;
import main.domain.port.PostCommentRepPort;
import main.domain.port.PostRepositoryPort;
import main.domain.port.PostVoteRepositoryPort;
import main.domain.port.UserRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Transactional(readOnly = true)
public class PostUseCase {
    private final PostRepositoryPort postRepositoryPort;
    private final PostVoteRepositoryPort postVoteRepositoryPort;
    private final PostCommentRepPort postCommentRepPort;
    private final UserRepositoryPort userRepositoryPort;

    public PostUseCase(PostRepositoryPort postRepositoryPort,
                       PostVoteRepositoryPort postVoteRepositoryPort,
                       PostCommentRepPort postCommentRepPort,
                       UserRepositoryPort userRepositoryPort) {
        this.postRepositoryPort = postRepositoryPort;
        this.postVoteRepositoryPort = postVoteRepositoryPort;
        this.postCommentRepPort = postCommentRepPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    public Iterable<Post> getPosts(int offset, int limit, String mode) {
        Iterable<Post> posts = null;
        List<Integer> postsId = new ArrayList<>();
        switch (mode) {
            case "recent":
                posts = postRepositoryPort.findPostsByActiveAndModerationStatusAndTimeLessThanEqual(
                        true,
                        ModerationStatus.ACCEPTED,
                        LocalDateTime.now(),
                        PageRequest.of(offset / limit, limit, Sort.by("time").descending()));
                break;
            case "popular":
                List<CommentCount> commentCounts = postCommentRepPort.getPopularPostId(PageRequest.of(offset / limit, limit));
                for (CommentCount commentCount : commentCounts) {
                    postsId.add(commentCount.getPostId());
                }
                posts = postRepositoryPort.findAllById(postsId);
                break;
            case "best":
                List<LikeCount> likeCounts = postVoteRepositoryPort.getBestPostsId(PageRequest.of(offset / limit, limit));
                for (LikeCount likeCount : likeCounts) {
                    postsId.add(likeCount.getPostId());
                }
                posts = postRepositoryPort.findAllById(postsId);
                break;
            case "early":
                posts = postRepositoryPort.findPostsByActiveAndModerationStatusAndTimeLessThanEqual(
                        true,
                        ModerationStatus.ACCEPTED,
                        LocalDateTime.now(),
                        PageRequest.of(offset / limit, limit, Sort.by("time")));
                break;
        }
        return posts;
    }

    public int getPostCount() {
        return postRepositoryPort.getPostCount();
    }

    public List<Post> searchPost(String query) {
        List<Post> posts;
        if (query.isEmpty()) {
            posts = postRepositoryPort.findPostsByActiveAndModerationStatusAndTimeLessThanEqual(
                    true,
                    ModerationStatus.ACCEPTED,
                    LocalDateTime.now(),
                    null
            );
        } else {
            posts = postRepositoryPort.findPostsByActiveAndModerationStatusAndTimeLessThanEqualAndTextContains(
                    true,
                    ModerationStatus.ACCEPTED,
                    LocalDateTime.now(),
                    query
            );
        }
        return posts;
    }

    public Optional<Post> getPostById(int id) {
        return postRepositoryPort.findPostByIdAndActiveAndModerationStatusAndTimeLessThanEqual(
                id,
                true,
                ModerationStatus.ACCEPTED,
                LocalDateTime.now()
        );
    }

    public List<Post> getPostsByDate(String date) {
        LocalDateTime localDateTime = LocalDate.parse(date).atStartOfDay();
        return postRepositoryPort.findPostsByActiveAndModerationStatusAndTimeBetween(
                true,
                ModerationStatus.ACCEPTED,
                localDateTime,
                localDateTime.plusDays(1).minusSeconds(1)
        );
    }

    public List<Post> getPostsByTag(String tag) {
        return postRepositoryPort.getPostByTag(tag);
    }

    public List<Post> getPostByModeration(int userId, int offset, int limit, String status) {
        Optional<User> findUser = userRepositoryPort.findById(userId);
        List<Post> posts = new ArrayList<>();
        if (findUser.isPresent()) {
            User user = findUser.get();
            if (user.isModerator()) {
                if (status.equals("new")) {
                    posts = postRepositoryPort.findPostsByActiveAndModerationStatus(
                            true,
                            ModerationStatus.NEW,
                            PageRequest.of(offset / limit, limit)
                    );
                } else {
                    posts = postRepositoryPort.findPostsByActiveAndModeratorIdAndModerationStatus(
                            true,
                            user.getId(),
                            ModerationStatus.valueOf(status.toUpperCase()),
                            PageRequest.of(offset / limit, limit)
                    );
                }
            }
        }
        return posts;
    }

    public List<Post> getMyPost(int userId, int offset, int limit, String status) {
        List<Post> posts = new ArrayList<>();
        switch (status) {
            case "inactive":
                posts = postRepositoryPort.findPostsByIdAndActive(userId, false, PageRequest.of(offset / limit, limit));
                break;
            case "pending":
                posts = postRepositoryPort.findPostsByUserIdAndActiveAndModerationStatus(
                        userId,
                        true,
                        ModerationStatus.NEW,
                        PageRequest.of(offset / limit, limit));
                break;
            case "declined":
                posts = postRepositoryPort.findPostsByUserIdAndActiveAndModerationStatus(
                        userId,
                        true,
                        ModerationStatus.DECLINED,
                        PageRequest.of(offset / limit, limit));
                break;
            case "published":
                posts = postRepositoryPort.findPostsByUserIdAndActiveAndModerationStatus(
                        userId,
                        true,
                        ModerationStatus.ACCEPTED,
                        PageRequest.of(offset / limit, limit));
                break;
        }
        return posts;
    }

    public void addPost(int userId, NewPostDto newPost) {
        Post post = new Post();
        post.setActive(newPost.getActive());
        post.setText(newPost.getText());
        post.setTitle(newPost.getTitle());
        post.setTime(LocalDateTime.parse(newPost.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        post.setModerationStatus(ModerationStatus.NEW);
        post.setUserId(userId);
        post.setTags(getTagList(newPost.getTags()));
        postRepositoryPort.save(post);
    }

    public HashMap<String, String> checkPostErrors(String text, String title) {
        HashMap<String, String> errors = new HashMap<>();
        if (title.isEmpty() || title.length() < 10) {
            errors.put("title", "Заголовок не установлен");
        }
        if (text.isEmpty() || text.length() < 500) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        return errors;
    }

    public boolean editPost(int userId, int postId, NewPostDto newPost) {
        Optional<Post> findPost = postRepositoryPort.findById(postId);
        if (findPost.isPresent()) {
            Optional<User> findUser = userRepositoryPort.findById(userId);
            if (findUser.isPresent()) {
                User user = findUser.get();
                Post post = findPost.get();
                if (!user.isModerator()) {
                    post.setModerationStatus(ModerationStatus.NEW);
                }
                post.setActive(newPost.getActive());
                post.setText(newPost.getText());
                post.setTitle(newPost.getTitle());
                post.setTime(LocalDateTime.parse(newPost.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                post.setTags(getTagList(newPost.getTags()));
                postRepositoryPort.save(post);
                return true;
            }
        }
        return false;
    }

    public boolean votePost(int userId, int postId, int value) {
        Optional<PostVote> postVote = postVoteRepositoryPort.findByUserIdEqualsAndPostIdEquals(userId, postId);
        if (postVote.isPresent()) {
            if (postVote.get().getValue() == value) {
                return false;
            } else {
                postVoteRepositoryPort.delete(postVote.get());
            }
        }
        PostVote newPostVote = new PostVote();
        newPostVote.setPostId(postId);
        newPostVote.setUserId(userId);
        newPostVote.setTime(LocalDateTime.now());
        newPostVote.setValue(value);
        postVoteRepositoryPort.save(newPostVote);
        return true;
    }

    public boolean setModerationStatus(int postId, int moderatorId, String decision) {
       // HashMap<String, String> message = new HashMap<>();
        Optional<Post> findPost = postRepositoryPort.findById(postId);
        if (findPost.isPresent()) {
            Post post = findPost.get();
            switch (decision) {
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
            post.setModeratorId(moderatorId);
            postRepositoryPort.save(post);
            return true;
        } else {
            return false;
            //message.put("message", "Пост не существует");
        }
    }

    public List<PostByYearCount> getPostsByYearCount(String year) {
        String searchYear = year;
        if (year.isEmpty()) {
            searchYear = Integer.toString(LocalDate.now().getYear());
        }
        return postRepositoryPort.getYearCounts(searchYear);
    }

    public LinkedList<Integer> getYears() {
        return postRepositoryPort.getYears();
    }

    public LinkedHashMap<String, Object> getStatisticsByUser(int userId) {
        List<Post> posts = postRepositoryPort.findAllByUserId(userId);
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
        return statistics;
    }

    public LinkedHashMap<String, Object> getAllStatistics() {
        LinkedHashMap<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("postsCount", postRepositoryPort.getPostCount());
        statistics.put("likesCount", postVoteRepositoryPort.getLikeCount());
        statistics.put("dislikesCount", postVoteRepositoryPort.getDislikeCount());
        statistics.put("viewsCount", postRepositoryPort.getViewCount());
        statistics.put("firstPublication", postRepositoryPort.getFirstPostDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return statistics;
    }

    private List<Tag> getTagList(String[] tags) {
        List<Tag> tagsList = new ArrayList<>();
        for (String tag : tags) {
            tagsList.add(new Tag(tag));
        }
        return tagsList;
    }
}