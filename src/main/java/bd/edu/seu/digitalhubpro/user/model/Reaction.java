package bd.edu.seu.digitalhubpro.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "video_user_unique", def = "{ 'videoId': 1, 'userId': 1 }", unique = true)
})
public class Reaction {
    @Id
    private String id;
    private String videoId;
    private String userId;
    private ReactionType type;
    private LocalDateTime createdAt;

    public Reaction() {
    }

    public Reaction(String videoId, String userId, ReactionType type, LocalDateTime createdAt) {
        this.videoId = videoId;
        this.userId = userId;
        this.type = type;
        this.createdAt = createdAt;
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

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ReactionType getType() {
        return this.type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


