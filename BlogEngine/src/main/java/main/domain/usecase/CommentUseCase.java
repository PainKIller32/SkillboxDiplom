package main.domain.usecase;

import main.domain.dto.NewCommentDto;
import main.domain.model.PostComment;
import main.domain.port.PostCommentRepPort;
import main.domain.port.PostRepositoryPort;
import main.domain.service.UserSecurity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional(readOnly = true)
public class CommentUseCase {
    private final PostCommentRepPort postCommentRepPort;
    private final UserSecurity userSecurity;
    private final PostRepositoryPort postRepositoryPort;

    public CommentUseCase(PostCommentRepPort postCommentRepPort,
                          UserSecurity userSecurity,
                          PostRepositoryPort postRepositoryPort) {
        this.postCommentRepPort = postCommentRepPort;
        this.userSecurity = userSecurity;
        this.postRepositoryPort = postRepositoryPort;
    }

    public int addComment(NewCommentDto comment, String sessionId) {
        PostComment postComment = new PostComment();
        postComment.setText(comment.getText());
        postComment.setPostId(comment.getPostId());
        postComment.setTime(LocalDateTime.now());
        postComment.setParentId(comment.getParentId());
        postComment.setUserId(userSecurity.getAuthorizedUserId(sessionId));
        return postCommentRepPort.save(postComment);
    }

    public boolean checkTextErrors(String text) {
        return text.isEmpty() || text.length() < 2;
    }

    public boolean checkExistence(NewCommentDto comment) {
        return !postRepositoryPort.existsById(comment.getPostId()) ||
                (comment.getParentId() != null && !postCommentRepPort.existsById(comment.getParentId()));
    }
}