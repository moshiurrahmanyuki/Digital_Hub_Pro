package bd.edu.seu.digitalhubpro.user.repository;

import bd.edu.seu.digitalhubpro.user.model.ShortVideo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortVideoRepository extends MongoRepository<ShortVideo, String> {
}


