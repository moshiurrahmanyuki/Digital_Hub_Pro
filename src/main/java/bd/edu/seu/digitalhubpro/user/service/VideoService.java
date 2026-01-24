package bd.edu.seu.digitalhubpro.user.service;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.user.dto.SearchRequest;
import bd.edu.seu.digitalhubpro.user.model.Video;
import bd.edu.seu.digitalhubpro.user.repository.VideoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VideoService {
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final MongoTemplate mongoTemplate;

    public VideoService(MemberRepository memberRepository, VideoRepository videoRepository, MongoTemplate mongoTemplate) {
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Video save(Video video, MultipartFile videoFile) {
        Long durationInSeconds = this.calculateVideoDuration(videoFile);
        video.setDurationSeconds(durationInSeconds);
        if (video.getUploadDate() == null) {
            video.setUploadDate(LocalDateTime.now());
        }

        return (Video)this.videoRepository.save(video);
    }

    private Long calculateVideoDuration(MultipartFile videoFile) {
        try {
            return (long)(Math.random() * (double)300.0F) + 10L;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Video> getAllVideos() {
        return this.videoRepository.findAll();
    }

    public void deleteByVideoById(String id) {
        this.videoRepository.deleteById(id);
    }

    public Optional<Video> findById(String id) {
        return this.videoRepository.findById(id);
    }

    public Optional<Video> getVideoById(String id) {
        return this.videoRepository.findById(id);
    }

    public Optional<Video> assignUploader(String videoId, String memberId) {
        Optional<Video> opt = this.videoRepository.findById(videoId);
        if (opt.isPresent()) {
            Video v = opt.get();
            // old: v.setUploaderId(memberId);
            v.setUploaderId(memberId);
            v.setUserId(memberId);
            this.videoRepository.save(v);
            return Optional.of(v);
        }
        return Optional.empty();
    }

    public void incrementViewCount(String videoId) {
        Query query = new Query(Criteria.where("id").is(videoId));
        Update update = (new Update()).inc("viewCount", 1);
        this.mongoTemplate.updateFirst(query, update, Video.class);
    }

    public List<Video> getVideoSearch(SearchRequest dto) {
        String searchTerm = dto.searchText();
        return searchTerm != null && !searchTerm.trim().isEmpty() ? this.videoRepository.searchVideosByKeyword(searchTerm) : this.getAllVideos();
    }

    private void populateUploader(Video video) {
        if (video.getUploaderId() != null) {
            Optional<Member> uploaderOptional = this.memberRepository.findById(video.getUploaderId());
            if (uploaderOptional.isPresent()) {
                Member uploader = (Member)uploaderOptional.get();
                uploader.getPhotosImagePath();
                PrintStream var10000 = System.out;
                String var10001 = uploader.getName();
                var10000.println("Uploader photo path for " + var10001 + ": " + uploader.getPhotosImagePath());
                video.setUploader(uploader);
            }
        }

    }

    public List<Video> AllVideos() {
        List<Video> videos = this.videoRepository.findAll();
        videos.forEach(this::populateUploader);
        return videos;
    }
//
//    private void populateVideoUser(Video video) {
//        if (video.getUserId() != null) {
//            Optional var10000 = this.memberRepository.findById(video.getUserId());
//            Objects.requireNonNull(video);
//            var10000.ifPresent(video ::setUploader);
//        }
//
//    }


    public void populateVideoUser(Video video) {
        if (video.getUserId() != null) {
            Optional<Member> userOptional = memberRepository.findById(video.getUserId());
            userOptional.ifPresent(user -> video.setUploader(user));
        }
    }


    public List<Video> getShortsVideos(Long maxDurationSeconds) {
        List<Video> shorts = this.videoRepository.findByDurationSecondsLessThanEqual(maxDurationSeconds);
        shorts.forEach(this::populateVideoUser);
        return shorts;
    }

    public List<Video> getRelatedVideos(String currentVideoId) {
        List<Video> relatedVideos = this.videoRepository.findAll();
        relatedVideos.removeIf((v) -> v.getId().equals(currentVideoId));
        relatedVideos.forEach(this::populateUploader);
        return relatedVideos.size() > 10 ? relatedVideos.subList(0, 10) : relatedVideos;
    }
}
