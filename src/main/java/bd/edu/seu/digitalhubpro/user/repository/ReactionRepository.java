package bd.edu.seu.digitalhubpro.user.repository;

import bd.edu.seu.digitalhubpro.user.model.Reaction;
import bd.edu.seu.digitalhubpro.user.model.ReactionType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {
    Optional<Reaction> findByVideoIdAndUserId(String videoId, String userId);
    long countByVideoIdAndType(String videoId, ReactionType type);
    List<Reaction> findByVideoId(String videoId);
}


