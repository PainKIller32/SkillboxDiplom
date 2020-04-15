package main.repository;

import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostByYearCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    List<Post> findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqual(int active, ModerationStatus moderationStatus, LocalDateTime date, Pageable page);

    List<Post> findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqualAndTextContains(int active, ModerationStatus moderationStatus, LocalDateTime date, String query);

    Optional<Post> findPostByIdAndIsActiveEqualsAndModerationStatusEqualsAndTimeLessThanEqual(int id, int active, ModerationStatus moderationStatus, LocalDateTime date);

    List<Post> findPostsByIsActiveEqualsAndModerationStatusEqualsAndTimeBetween(int active, ModerationStatus moderationStatus, LocalDateTime startDay, LocalDateTime endDay);

    List<Post> findPostsByIsActiveEqualsAndModeratorIdEqualsAndModerationStatusEquals(int active, int moderatorId, ModerationStatus moderationStatus, Pageable page);

    List<Post> findPostsByIsActiveEqualsAndModerationStatusEquals(int active, ModerationStatus moderationStatus, Pageable page);

    List<Post> findPostsByIdAndIsActiveEquals(int id, int active, Pageable page);

    List<Post> findPostsByUserIdEqualsAndIsActiveEqualsAndModerationStatusEquals(int userId, int active, ModerationStatus moderationStatus, Pageable page);

    List<Post> findAllByUserIdEquals(int userId);

    @Query(value = "SELECT p FROM Post as p " +
            "INNER JOIN TagToPost AS t2p ON p.id = t2p.postId " +
            "INNER JOIN Tag as t ON t2p.tagId = t.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND t.name = :tag")
    List<Post> getPostByTag(String tag);

    @Query("SELECT COUNT (ALL p.id) FROM Post AS p WHERE p.moderatorId = :id")
    int getModerationPostCount(int id);

    @Query("SELECT COUNT(ALL p.id ) FROM Post AS p")
    int getPostCount();

    @Query("SELECT COUNT(ALL p.id ) FROM Post AS p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < current_date ")
    int getViewPostCount();

    @Query("SELECT SUM(p.viewCount) FROM Post AS p")
    int getViewCount();

    @Query("SELECT MIN(p.time) FROM Post AS p")
    LocalDateTime getFirstPostDate();

    @Query(value = "SELECT p.time as time, count(all p.id) as count FROM posts as p where year(p.time) = :year group by date(time) order by count desc", nativeQuery = true)
    List<PostByYearCount> getYearCounts(String year);

    @Query(value = "SELECT year(p.time) as time FROM posts as p group by year(time)", nativeQuery = true)
    LinkedList<Integer> getYears();
}