package bd.edu.seu.digitalhubpro.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "subscriber_channel_unique", def = "{ 'subscriberId': 1, 'channelId': 1 }", unique = true)
})
public class Subscription {
    @Id
    private String id;
    private String subscriberId;
    private String channelId;
    private LocalDateTime createdAt;

    public Subscription() {}

    public Subscription(String subscriberId, String channelId, LocalDateTime createdAt) {
        this.subscriberId = subscriberId;
        this.channelId = channelId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSubscriberId() { return subscriberId; }
    public void setSubscriberId(String subscriberId) { this.subscriberId = subscriberId; }
    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


