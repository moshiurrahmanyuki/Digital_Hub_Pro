package bd.edu.seu.digitalhubpro.user.repository;

import bd.edu.seu.digitalhubpro.user.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    List<Video> findByDurationSecondsLessThanEqual(Long maxDuration);

    @Query("{ $or: [ { 'title' : { $regex : ?#{[0]}, $options : 'i' } }, { 'description' : { $regex : ?#{[0]}, $options : 'i' } }, { 'tags' : { $regex : ?#{[0]}, $options : 'i' } } ] }")
    List<Video> searchVideosByKeyword(String searchTerm);
}
