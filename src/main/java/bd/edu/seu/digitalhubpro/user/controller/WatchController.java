package bd.edu.seu.digitalhubpro.user.controller;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import bd.edu.seu.digitalhubpro.user.model.Comment;
import bd.edu.seu.digitalhubpro.user.model.TimeAgoConverter;
import bd.edu.seu.digitalhubpro.user.model.Video;
import bd.edu.seu.digitalhubpro.user.service.CommentService;
import bd.edu.seu.digitalhubpro.user.service.VideoService;
import bd.edu.seu.digitalhubpro.user.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class WatchController {
    private final VideoService videoService;
    private final CommentService commentService;
    private final TimeAgoConverter timeAgoConverter;
    private final MemberRepository memberRepository;
    private final SubscriptionService subscriptionService;

    public WatchController(VideoService videoService, CommentService commentService, TimeAgoConverter timeAgoConverter, MemberRepository memberRepository, SubscriptionService subscriptionService) {
        this.videoService = videoService;
        this.commentService = commentService;
        this.timeAgoConverter = timeAgoConverter;
        this.memberRepository = memberRepository;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping({"/watch/{id}"})
    public String watchVideo(@PathVariable String id, Model model) {
        List<Video> videos = this.videoService.AllVideos();
        model.addAttribute("thumbnail", videos);
        Optional<Video> videoOptional = this.videoService.findById(id);
        if (videoOptional.isPresent()) {
            Video video = (Video)videoOptional.get();
            // Populate uploader so channel name/avatar work on watch page
            try {
                if (video.getUploaderId() != null) {
                    this.memberRepository.findById(video.getUploaderId()).ifPresent(video::setUploader);
                } else if (video.getUserId() != null) {
                    this.memberRepository.findById(video.getUserId()).ifPresent(video::setUploader);
                }
            } catch (Exception ignore) {}
            video.setTimeAgo(this.timeAgoConverter.convert(video.getUploadDate()));
            model.addAttribute("video", video);
            List<Comment> comment = this.commentService.getCommentsForVideo(id);
            model.addAttribute("comments", comment);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Member loggedInUser = null;
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof MemberUserDetails) {
                    MemberUserDetails userDetails = (MemberUserDetails)principal;
                    loggedInUser = userDetails.getMember();
                } else if (principal instanceof String) {
                    String email = (String)principal;
                    loggedInUser = (Member)this.memberRepository.findByEmail(email).orElse(null);
                }
            }

            model.addAttribute("loggedInUser", loggedInUser);
            if (video.getUploader() == null && loggedInUser != null) {
                video.setUploader(loggedInUser);
            }
            if (video.getUploaderId() != null) {
                long subs = this.subscriptionService.countSubscribers(video.getUploaderId());
                model.addAttribute("subscriberCount", subs);
            } else if (loggedInUser != null) {
                long subs = this.subscriptionService.countSubscribers(loggedInUser.getId());
                model.addAttribute("subscriberCount", subs);
            }
            List<Video> relatedVideos = this.videoService.getRelatedVideos(id);
            model.addAttribute("relatedVideos", relatedVideos);
            if (video.getUploadDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd,yyyy");
                model.addAttribute("formattedUploadDate", video.getUploadDate().format(formatter));
            }

            return "user/showVideos";
        } else {
            Message var10000 = new Message("error", "Video not found!");
            return "redirect:/add-video?message=" + String.valueOf(var10000);
        }
    }

    @PostMapping({"/api/videos/{id}/increment-view"})
    @ResponseBody
    public ResponseEntity<Void> incrementViewCountApi(@PathVariable String id) {
        try {
            this.videoService.incrementViewCount(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error incrementing view count for video ID " + id + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping({"/api/videos/{videoId}/comments/{commentId}"})
    @ResponseBody
    public ResponseEntity<Void> deleteComment(@PathVariable String videoId, @PathVariable String commentId) {
        try {
            this.commentService.getCommentById(commentId).ifPresent(c -> {
                this.commentService.incrementLikeCount(commentId); // no-op placeholder to preserve old code style
            });
            // actually delete
            this.commentService.getCommentById(commentId).ifPresent(c -> {
                // old: repository.delete(c)
                this.commentService.deleteById(commentId);
            });
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping({"/api/videos/{videoId}/comments"})
    @ResponseBody
    public ResponseEntity<Comment> addComment(@PathVariable String videoId, @RequestBody Map<String, String> payload, Authentication authentication, Model model) {
        String commentText = (String)payload.get("commentText");
        String authorName = (String)payload.get("authorName");
        String parentCommentId = payload.get("parentCommentId");
        Member loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof MemberUserDetails) {
                MemberUserDetails userDetails = (MemberUserDetails)principal;
                authorName = userDetails.getMember().getName();
                loggedInUser = userDetails.getMember();
            } else if (principal instanceof String) {
                String email = (String)principal;
                Optional<Member> member = this.memberRepository.findByEmail(email);
                if (member.isPresent()) {
                    authorName = ((Member)member.get()).getName();
                    loggedInUser = (Member)member.get();
                }
            }
        }

        model.addAttribute("loggedInUser", loggedInUser);
        if (authorName == null || authorName.isEmpty() || authorName.equals("GuestUser")) {
            authorName = "GuestUser";
        }

        if (commentText != null && !commentText.trim().isEmpty() && videoId != null) {
            try {
                Comment newComment;
                if (parentCommentId != null && !parentCommentId.trim().isEmpty()) {
                    newComment = this.commentService.saveReply(videoId, authorName, commentText, parentCommentId);
                } else {
                    newComment = this.commentService.saveComment(videoId, authorName, commentText);
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
            } catch (Exception e) {
                System.err.println("Error adding comment to video " + videoId + ": " + e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping({"/api/videos/{videoId}/comments/{commentId}/replies"})
    @ResponseBody
    public ResponseEntity<List<Comment>> getReplies(@PathVariable String videoId, @PathVariable String commentId) {
        try {
            List<Comment> replies = this.commentService.getReplies(videoId, commentId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            System.err.println("Error loading replies for comment " + commentId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping({"/api/videos/{videoId}/comments/{commentId}/like"})
    @ResponseBody
    public ResponseEntity<Comment> likeComment(@PathVariable String videoId, @PathVariable String commentId) {
        try {
            Optional<Comment> updatedComment = this.commentService.incrementLikeCount(commentId);
            return updatedComment.isPresent() ? ResponseEntity.ok((Comment)updatedComment.get()) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error liking comment " + commentId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping({"/api/videos/{videoId}/comments/{commentId}/dislike"})
    @ResponseBody
    public ResponseEntity<Comment> dislikeComment(@PathVariable String videoId, @PathVariable String commentId) {
        try {
            Optional<Comment> updatedComment = this.commentService.incrementDislikeCount(commentId);
            return updatedComment.isPresent() ? ResponseEntity.ok((Comment)updatedComment.get()) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error disliking comment " + commentId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public static class Message {
        private String type;
        private String text;

        public Message(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public String getType() {
            return this.type;
        }

        public String getText() {
            return this.text;
        }
    }
}
