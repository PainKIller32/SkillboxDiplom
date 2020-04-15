package main.repository;

import main.model.LikeCount;
import main.model.PostVote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {
    @Query("SELECT pv.postId AS postId, count(pv.value) AS likeCount " +
            "FROM PostVote AS pv " +
            "INNER JOIN Post AS p ON pv.postId = p.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' " +
            "AND pv.value > 0 " +
            "group by pv.postId order by likeCount DESC")
    List<LikeCount> getBestPostsId(Pageable page);

    Optional<PostVote> findByUserIdEqualsAndPostIdEquals(int userId, int postId);

    @Query("SELECT COUNT(ALL pv.id ) FROM PostVote AS pv WHERE pv.value = 1")
    int getLikeCount();

    @Query("SELECT COUNT(ALL pv.id ) FROM PostVote AS pv WHERE pv.value = -1")
    int getDislikeCount();
}