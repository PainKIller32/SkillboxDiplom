package main.dao.repositoryPortImpl;

import main.dao.repository.PostCommentRepository;
import main.domain.model.CommentCount;
import main.domain.model.PostComment;
import main.domain.port.PostCommentRepPort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostCommentRepPortImpl implements PostCommentRepPort {
    private final PostCommentRepository commentRepository;

    public PostCommentRepPortImpl(PostCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<CommentCount> getPopularPostId(Pageable page) {
        return commentRepository.getPopularPostId(page);
    }

    @Override
    public int save(PostComment comment) {
        return commentRepository.save(comment).getId();
    }

    @Override
    public boolean existsById(int id) {
        return commentRepository.existsById(id);
    }
}
