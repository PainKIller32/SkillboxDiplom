package main.domain.port;

import main.domain.model.ModerationStatus;
import main.domain.model.Post;
import main.domain.model.PostByYearCount;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    List<Post> findPostsByIsActiveAndModerationStatusAndTimeLessThanEqual(int active, ModerationStatus moderationStatus, LocalDateTime date, Pageable page);

    List<Post> findPostsByIsActiveAndModerationStatusAndTimeLessThanEqualAndTextContains(int active, ModerationStatus moderationStatus, LocalDateTime date, String query);

    Optional<Post> findPostByIdAndIsActiveAndModerationStatusAndTimeLessThanEqual(int id, int active, ModerationStatus moderationStatus, LocalDateTime date);

    List<Post> findPostsByIsActiveAndModerationStatusAndTimeBetween(int active, ModerationStatus moderationStatus, LocalDateTime startDay, LocalDateTime endDay);

    List<Post> findPostsByIsActiveAndModeratorIdAndModerationStatus(int active, int moderatorId, ModerationStatus moderationStatus, Pageable page);

    List<Post> findPostsByIsActiveAndModerationStatus(int active, ModerationStatus moderationStatus, Pageable page);

    List<Post> findPostsByIdAndIsActive(int id, int active, Pageable page);

    List<Post> findPostsByUserIdAndIsActiveAndModerationStatus(int userId, int active, ModerationStatus moderationStatus, Pageable page);

    List<Post> findAllByUserId(int userId);

    List<Post> getPostByTag(String tag);

    int getModerationPostCount(int id);

    int getPostCount();

    int getViewPostCount();

    int getViewCount();

    LocalDateTime getFirstPostDate();

    List<PostByYearCount> getYearCounts(String year);

    LinkedList<Integer> getYears();

    Iterable<Post> findAllById(List<Integer> postsId);

    void save(Post post);

    Optional<Post> findById(int postId);

    boolean existsById(int id);
}
