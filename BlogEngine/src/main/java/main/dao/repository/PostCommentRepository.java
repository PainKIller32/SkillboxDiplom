package main.dao.repository;

import main.domain.model.CommentCount;
import main.domain.model.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Integer> {
    @Query("SELECT pc.postId AS postId, count(pc.id) AS c " +
            "FROM PostComment AS pc " +
            "INNER JOIN Post AS p ON pc.postId = p.id " +
            "WHERE p.active = true AND p.moderationStatus = 'ACCEPTED' " +
            "GROUP BY pc.postId ORDER BY c DESC")
    List<CommentCount> getPopularPostId(Pageable page);
}