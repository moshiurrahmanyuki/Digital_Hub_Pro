package bd.edu.seu.digitalhubpro.user.controller;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import bd.edu.seu.digitalhubpro.user.model.Subscription;
import bd.edu.seu.digitalhubpro.user.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final MemberRepository memberRepository;

    public SubscriptionController(SubscriptionService subscriptionService, MemberRepository memberRepository) {
        this.subscriptionService = subscriptionService;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/channels/{channelId}")
    public ResponseEntity<Long> subscribe(@PathVariable String channelId, Authentication authentication) {
        String userId = resolveUserId(authentication);
        if (userId == null) return ResponseEntity.status(401).build();
        Subscription s = this.subscriptionService.subscribe(userId, channelId);
        long count = this.subscriptionService.countSubscribers(channelId);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<Long> unsubscribe(@PathVariable String channelId, Authentication authentication) {
        String userId = resolveUserId(authentication);
        if (userId == null) return ResponseEntity.status(401).build();
        this.subscriptionService.unsubscribe(userId, channelId);
        long count = this.subscriptionService.countSubscribers(channelId);
        return ResponseEntity.ok(count);
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> mySubscriptions(Authentication authentication) {
        String userId = resolveUserId(authentication);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(this.subscriptionService.getSubscriptions(userId));
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


