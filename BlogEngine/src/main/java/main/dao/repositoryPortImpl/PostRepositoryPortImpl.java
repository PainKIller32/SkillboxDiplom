package main.dao.repositoryPortImpl;

import main.dao.repository.PostRepository;
import main.domain.model.ModerationStatus;
import main.domain.model.Post;
import main.domain.model.PostByYearCount;
import main.domain.port.PostRepositoryPort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class PostRepositoryPortImpl implements PostRepositoryPort {
    private final PostRepository postRepository;

    public PostRepositoryPortImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> findPostsByIsActiveAndModerationStatusAndTimeLessThanEqual(int active, ModerationStatus moderationStatus, LocalDateTime date, Pageable page) {
        return postRepository.findPostsByIsActiveAndModerationStatusAndTimeLessThanEqual(active, moderationStatus, date, page);
    }

    @Override
    public List<Post> findPostsByIsActiveAndModerationStatusAndTimeLessThanEqualAndTextContains(int active, ModerationStatus moderationStatus, LocalDateTime date, String query) {
        return postRepository.findPostsByIsActiveAndModerationStatusAndTimeLessThanEqualAndTextContains(active, moderationStatus, date, query);
    }

    @Override
    public Optional<Post> findPostByIdAndIsActiveAndModerationStatusAndTimeLessThanEqual(int id, int active, ModerationStatus moderationStatus, LocalDateTime date) {
        return postRepository.findPostByIdAndIsActiveAndModerationStatusAndTimeLessThanEqual(id, active, moderationStatus, date);
    }

    @Override
    public List<Post> findPostsByIsActiveAndModerationStatusAndTimeBetween(int active, ModerationStatus moderationStatus, LocalDateTime startDay, LocalDateTime endDay) {
        return postRepository.findPostsByIsActiveAndModerationStatusAndTimeBetween(active, moderationStatus, startDay, endDay);
    }

    @Override
    public List<Post> findPostsByIsActiveAndModeratorIdAndModerationStatus(int active, int moderatorId, ModerationStatus moderationStatus, Pageable page) {
        return postRepository.findPostsByIsActiveAndModeratorIdAndModerationStatus(active, moderatorId, moderationStatus, page);
    }

    @Override
    public List<Post> findPostsByIsActiveAndModerationStatus(int active, ModerationStatus moderationStatus, Pageable page) {
        return postRepository.findPostsByIsActiveAndModerationStatus(active, moderationStatus, page);
    }

    @Override
    public List<Post> findPostsByIdAndIsActive(int id, int active, Pageable page) {
        return postRepository.findPostsByIdAndIsActive(id, active, page);
    }

    @Override
    public List<Post> findPostsByUserIdAndIsActiveAndModerationStatus(int userId, int active, ModerationStatus moderationStatus, Pageable page) {
        return postRepository.findPostsByUserIdAndIsActiveAndModerationStatus(userId, active, moderationStatus, page);
    }

    @Override
    public List<Post> findAllByUserId(int userId) {
        return postRepository.findAllByUserId(userId);
    }

    @Override
    public List<Post> getPostByTag(String tag) {
        return postRepository.getPostByTag(tag);
    }

    @Override
    public int getModerationPostCount(int id) {
        return postRepository.getModerationPostCount(id);
    }

    @Override
    public int getPostCount() {
        return postRepository.getPostCount();
    }

    @Override
    public int getViewPostCount() {
        return postRepository.getViewPostCount();
    }

    @Override
    public int getViewCount() {
        return postRepository.getViewCount();
    }

    @Override
    public LocalDateTime getFirstPostDate() {
        return postRepository.getFirstPostDate();
    }

    @Override
    public List<PostByYearCount> getYearCounts(String year) {
        return postRepository.getYearCounts(year);
    }

    @Override
    public LinkedList<Integer> getYears() {
        return postRepository.getYears();
    }

    @Override
    public Iterable<Post> findAllById(List<Integer> postsId) {
        return postRepository.findAllById(postsId);
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(int postId) {
        return postRepository.findById(postId);
    }

    @Override
    public boolean existsById(int id) {
        return postRepository.existsById(id);
    }
}