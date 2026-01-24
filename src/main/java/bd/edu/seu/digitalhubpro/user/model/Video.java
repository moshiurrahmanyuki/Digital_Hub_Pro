package bd.edu.seu.digitalhubpro.user.model;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document
public class Video {
    @Id
    private String id;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDateTime uploadDate;
    private String userId;
    private String category;
    private List<String> tags;
    private long viewCount = 0L;
    private String duration;
    private Long durationSeconds;
    private transient String timeAgo;
    private Long likeCount = 0L;
    @Field("uploaderId")
    private String uploaderId;
    @Transient
    private Member uploader;

    public Video() {
    }

    public Video(String id, String title, String description, String videoUrl, String thumbnailUrl, LocalDateTime uploadDate, String userId, String category, List<String> tags, long viewCount, String duration, Long durationSeconds, String timeAgo, Long likeCount, String uploaderId, Member uploader, String videoPath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.uploadDate = uploadDate;
        this.userId = userId;
        this.category = category;
        this.tags = tags;
        this.viewCount = viewCount;
        this.duration = duration;
        this.durationSeconds = durationSeconds;
        this.timeAgo = timeAgo;
        this.likeCount = likeCount;
        this.uploaderId = uploaderId;
        this.uploader = uploader;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public LocalDateTime getUploadDate() {
        return this.uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getViewCount() {
        return this.viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public String getVideoPath() {
        // Old: return "Upload_Videos/" + this.id + "/" + this.videoUrl;
        // New: absolute path so it works on nested routes like /home
        return "/Upload_Videos/" + this.id + "/" + this.videoUrl;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getDurationSeconds() {
        return this.durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getTimeAgo() {
        return this.timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getThumbnailPath() {
        // Old (relative path could break on nested routes):
        // return this.id != null && this.thumbnailUrl != null && !this.thumbnailUrl.isEmpty() ? "Upload_Thumbnails/" + this.id + "/" + this.thumbnailUrl : "https://i.ytimg.com/vi/6QE8dXq9SOE/maxresdefault.jpg";
        // New: ensure absolute path so Spring static handler "/Upload_Thumbnails/**" matches
        return this.id != null && this.thumbnailUrl != null && !this.thumbnailUrl.isEmpty()
                ? "/Upload_Thumbnails/" + this.id + "/" + this.thumbnailUrl
                : "https://i.ytimg.com/vi/6QE8dXq9SOE/maxresdefault.jpg";
    }

    public Long getLikeCount() {
        return this.likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public String getUploaderId() {
        return this.uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Member getUploader() {
        return this.uploader;
    }

    public void setUploader(Member uploader) {
        this.uploader = uploader;
    }
}
