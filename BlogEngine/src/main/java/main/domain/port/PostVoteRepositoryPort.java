package main.domain.port;

import main.domain.model.LikeCount;
import main.domain.model.PostVote;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostVoteRepositoryPort {
    List<LikeCount> getBestPostsId(Pageable page);

    Optional<PostVote> findByUserIdEqualsAndPostIdEquals(int userId, int postId);

    int getLikeCount();

    int getDislikeCount();

    void delete(PostVote postVote);

    void save(PostVote postVote);
}
