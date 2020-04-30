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
    List<Post> findPostsByActiveAndModerationStatusAndTimeLessThanEqual(boolean active, ModerationStatus moderationStatus, LocalDateTime date, Pageable page);

    List<Post> findPostsByActiveAndModerationStatusAndTimeLessThanEqualAndTextContains(boolean active, ModerationStatus moderationStatus, LocalDateTime date, String query);

    Optional<Post> findPostByIdAndActiveAndModerationStatusAndTimeLessThanEqual(int id, boolean active, ModerationStatus moderationStatus, LocalDateTime date);

    List<Post> findPostsByActiveAndModerationStatusAndTimeBetween(boolean active, ModerationStatus moderationStatus, LocalDateTime startDay, LocalDateTime endDay);

    List<Post> findPostsByActiveAndModeratorIdAndModerationStatus(boolean active, int moderatorId, ModerationStatus moderationStatus, Pageable page);

    List<Post> findPostsByActiveAndModerationStatus(boolean active, ModerationStatus moderationStatus, Pageable page);

    List<Post> findPostsByIdAndActive(int id, boolean active, Pageable page);

    List<Post> findPostsByUserIdAndActiveAndModerationStatus(int userId, boolean active, ModerationStatus moderationStatus, Pageable page);

    List<Post> findAllByUserId(int userId);

    List<Post> getPostByTag(String tag);

    int countByModeratorId(int id);

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
