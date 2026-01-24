package bd.edu.seu.digitalhubpro.user.service;

import bd.edu.seu.digitalhubpro.user.model.Subscription;
import bd.edu.seu.digitalhubpro.user.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription subscribe(String subscriberId, String channelId) {
        return this.subscriptionRepository.findBySubscriberIdAndChannelId(subscriberId, channelId)
                .orElseGet(() -> this.subscriptionRepository.save(new Subscription(subscriberId, channelId, LocalDateTime.now())));
    }

    public void unsubscribe(String subscriberId, String channelId) {
        this.subscriptionRepository.findBySubscriberIdAndChannelId(subscriberId, channelId)
                .ifPresent(s -> this.subscriptionRepository.deleteById(s.getId()));
    }

    public long countSubscribers(String channelId) {
        return this.subscriptionRepository.countByChannelId(channelId);
    }

    public List<Subscription> getSubscriptions(String subscriberId) {
        return this.subscriptionRepository.findBySubscriberId(subscriberId);
    }
}


