package main.dao.repositoryPortImpl;

import main.dao.repository.PostVoteRepository;
import main.domain.model.LikeCount;
import main.domain.model.PostVote;
import main.domain.port.PostVoteRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PostVoteRepositoryPortImpl implements PostVoteRepositoryPort {

    private final PostVoteRepository postVoteRepository;

    public PostVoteRepositoryPortImpl(PostVoteRepository postVoteRepository) {
        this.postVoteRepository = postVoteRepository;
    }

    @Override
    public List<LikeCount> getBestPostsId(Pageable page) {
        return postVoteRepository.getBestPostsId(page);
    }

    @Override
    public Optional<PostVote> findByUserIdEqualsAndPostIdEquals(int userId, int postId) {
        return postVoteRepository.findByUserIdEqualsAndPostIdEquals(userId, postId);
    }

    @Override
    public int getLikeCount() {
        return postVoteRepository.getLikeCount();
    }

    @Override
    public int getDislikeCount() {
        return postVoteRepository.getDislikeCount();
    }

    @Override
    public void delete(PostVote postVote) {
        postVoteRepository.delete(postVote);
    }

    @Override
    public void save(PostVote postVote) {
        postVoteRepository.save(postVote);
    }
}
