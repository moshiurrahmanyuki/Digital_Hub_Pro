package bd.edu.seu.digitalhubpro.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class Comment {
    @Id
    private String id;
    private String videoId;
    private String author;
    private String text;
    private LocalDateTime timestamp;
    private Long likeCount = 0L;
    private Long dislikeCount = 0L;
	// Added for threaded replies
	private String parentCommentId; // null for top-level
	private Integer depth; // 0 for top-level, increases for replies

    public Comment() {
    }

    public Comment(String videoId, String author, String text, LocalDateTime timestamp) {
        this.videoId = videoId;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoId() {
        return this.videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getLikeCount() {
        return this.likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getDislikeCount() {
        return this.dislikeCount;
    }

    public void setDislikeCount(Long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

	public String getParentCommentId() {
		return this.parentCommentId;
	}

	public void setParentCommentId(String parentCommentId) {
		this.parentCommentId = parentCommentId;
	}

	public Integer getDepth() {
		return this.depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}
}
