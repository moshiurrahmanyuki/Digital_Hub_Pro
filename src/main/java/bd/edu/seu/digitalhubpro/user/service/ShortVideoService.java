package bd.edu.seu.digitalhubpro.user.service;

import bd.edu.seu.digitalhubpro.user.model.ShortVideo;
import bd.edu.seu.digitalhubpro.user.repository.ShortVideoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShortVideoService {
    private final ShortVideoRepository shortVideoRepository;
    private final MongoTemplate mongoTemplate;

    public ShortVideoService(ShortVideoRepository shortVideoRepository, MongoTemplate mongoTemplate) {
        this.shortVideoRepository = shortVideoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<ShortVideo> findAll() { return this.shortVideoRepository.findAll(); }
    public Optional<ShortVideo> findById(String id) { return this.shortVideoRepository.findById(id); }
    public Optional<ShortVideo> getShortVideoById(String id) { return this.shortVideoRepository.findById(id); }

    public void incrementViewCount(String id) {
        Query q = new Query(Criteria.where("id").is(id));
        Update u = new Update().inc("viewCount", 1);
        this.mongoTemplate.updateFirst(q, u, ShortVideo.class);
    }

    // Compatibility helper: currently returns all shorts regardless of duration cap
    public List<ShortVideo> getShortsVideos(Long maxDurationSeconds) {
        // Previous implementation filtered by Video.durationSeconds.
        // For ShortVideo dedicated collection we return all for now.
        return this.findAll();
    }

    public ShortVideo save(ShortVideo shortVideo) {
        return this.shortVideoRepository.save(shortVideo);
    }
}


