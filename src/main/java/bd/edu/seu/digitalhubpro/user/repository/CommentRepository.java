package bd.edu.seu.digitalhubpro.user.repository;

import bd.edu.seu.digitalhubpro.user.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByVideoIdOrderByTimestampDesc(String videoId);
    List<Comment> findByVideoIdAndParentCommentIdOrderByTimestampAsc(String videoId, String parentCommentId);
}
