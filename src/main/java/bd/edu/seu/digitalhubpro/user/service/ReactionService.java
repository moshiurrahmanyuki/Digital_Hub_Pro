package bd.edu.seu.digitalhubpro.user.service;

import bd.edu.seu.digitalhubpro.user.model.Reaction;
import bd.edu.seu.digitalhubpro.user.model.ReactionType;
import bd.edu.seu.digitalhubpro.user.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public Reaction setReaction(String videoId, String userId, ReactionType type) {
        Optional<Reaction> existing = this.reactionRepository.findByVideoIdAndUserId(videoId, userId);
        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            // preserve old reaction as comment per guideline
            // reaction.setType(type);
            Reaction updated = new Reaction();
            updated.setId(reaction.getId());
            updated.setVideoId(reaction.getVideoId());
            updated.setUserId(reaction.getUserId());
            updated.setType(type);
            updated.setCreatedAt(reaction.getCreatedAt());
            return this.reactionRepository.save(updated);
        }
        Reaction reaction = new Reaction(videoId, userId, type, LocalDateTime.now());
        return this.reactionRepository.save(reaction);
    }

    public void removeReaction(String videoId, String userId) {
        this.reactionRepository.findByVideoIdAndUserId(videoId, userId).ifPresent(r -> this.reactionRepository.deleteById(r.getId()));
    }

    public Map<ReactionType, Long> getCounts(String videoId) {
        Map<ReactionType, Long> counts = new EnumMap<>(ReactionType.class);
        for (ReactionType type : ReactionType.values()) {
            counts.put(type, this.reactionRepository.countByVideoIdAndType(videoId, type));
        }
        return counts;
    }
}


