package main.domain.port;

import main.domain.model.CommentCount;
import main.domain.model.PostComment;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCommentRepPort {
    List<CommentCount> getPopularPostId(Pageable page);

    int save(PostComment comment);

    boolean existsById(int id);
}
