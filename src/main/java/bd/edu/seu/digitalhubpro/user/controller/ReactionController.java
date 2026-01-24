package bd.edu.seu.digitalhubpro.user.controller;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import bd.edu.seu.digitalhubpro.user.model.ReactionType;
import bd.edu.seu.digitalhubpro.user.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos/{videoId}/reactions")
public class ReactionController {
    private final ReactionService reactionService;
    private final MemberRepository memberRepository;

    public ReactionController(ReactionService reactionService, MemberRepository memberRepository) {
        this.reactionService = reactionService;
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity<Map<ReactionType, Long>> setReaction(
            @PathVariable String videoId,
            @RequestParam("type") ReactionType type,
            Authentication authentication
    ) {
        String userId = resolveUserId(authentication);
        if (userId == null) return ResponseEntity.status(401).build();
        this.reactionService.setReaction(videoId, userId, type);
        return ResponseEntity.ok(this.reactionService.getCounts(videoId));
    }

    @DeleteMapping
    public ResponseEntity<Map<ReactionType, Long>> removeReaction(
            @PathVariable String videoId,
            Authentication authentication
    ) {
        String userId = resolveUserId(authentication);
        if (userId == null) return ResponseEntity.status(401).build();
        this.reactionService.removeReaction(videoId, userId);
        return ResponseEntity.ok(this.reactionService.getCounts(videoId));
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<ReactionType, Long>> getCounts(@PathVariable String videoId) {
        return ResponseEntity.ok(this.reactionService.getCounts(videoId));
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof MemberUserDetails) {
            MemberUserDetails mud = (MemberUserDetails) principal;
            Member member = mud.getMember();
            return member != null ? member.getId() : null;
        } else if (principal instanceof String) {
            String email = (String) principal;
            Optional<Member> m = this.memberRepository.findByEmail(email);
            return m.map(Member::getId).orElse(null);
        }
        return null;
    }
}


