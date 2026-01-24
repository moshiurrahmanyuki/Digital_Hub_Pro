package bd.edu.seu.digitalhubpro.user.service;

import bd.edu.seu.digitalhubpro.user.model.Comment;
import bd.edu.seu.digitalhubpro.user.repository.CommentRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MongoTemplate mongoTemplate;

    public CommentService(CommentRepository commentRepository, MongoTemplate mongoTemplate) {
        this.commentRepository = commentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Comment saveComment(String videoId, String authorName, String text) {
        Comment comment = new Comment(videoId, authorName, text, LocalDateTime.now());
        comment.setLikeCount(0L);
        comment.setDislikeCount(0L);
        comment.setParentCommentId(null);
        comment.setDepth(0);
        return (Comment)this.commentRepository.save(comment);
    }

    public Comment saveReply(String videoId, String authorName, String text, String parentCommentId) {
        Comment comment = new Comment(videoId, authorName, text, LocalDateTime.now());
        comment.setLikeCount(0L);
        comment.setDislikeCount(0L);
        comment.setParentCommentId(parentCommentId);
        comment.setDepth(1);
        return (Comment)this.commentRepository.save(comment);
    }

    public List<Comment> getCommentsForVideo(String videoId) {
        return this.commentRepository.findByVideoIdOrderByTimestampDesc(videoId);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return this.commentRepository.findById(commentId);
    }

    public Optional<Comment> incrementLikeCount(String commentId) {
        Query query = new Query(Criteria.where("id").is(commentId));
        Update update = (new Update()).inc("likeCount", 1);
        return Optional.ofNullable((Comment)this.mongoTemplate.findAndModify(query, update, Comment.class));
    }

    public Optional<Comment> incrementDislikeCount(String commentId) {
        Query query = new Query(Criteria.where("id").is(commentId));
        Update update = (new Update()).inc("dislikeCount", 1);
        return Optional.ofNullable((Comment)this.mongoTemplate.findAndModify(query, update, Comment.class));
    }

    public Optional<Comment> decrementLikeCount(String commentId) {
        Query query = new Query(Criteria.where("id").is(commentId));
        Update update = (new Update()).inc("likeCount", -1);
        return Optional.ofNullable((Comment)this.mongoTemplate.findAndModify(query, update, Comment.class));
    }

    public Optional<Comment> decrementDislikeCount(String commentId) {
        Query query = new Query(Criteria.where("id").is(commentId));
        Update update = (new Update()).inc("dislikeCount", -1);
        return Optional.ofNullable((Comment)this.mongoTemplate.findAndModify(query, update, Comment.class));
    }

    public List<Comment> getReplies(String videoId, String parentCommentId) {
        return this.commentRepository.findByVideoIdAndParentCommentIdOrderByTimestampAsc(videoId, parentCommentId);
    }

    public void deleteById(String commentId) {
        this.commentRepository.deleteById(commentId);
    }
}
