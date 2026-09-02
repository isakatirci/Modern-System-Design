package com.systemdesign.feed.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Hybrid fanout: düşük follower sayısında push, celebrity hesaplarda pull. */
@Service
public class TimelineService {

    private final int pushFollowerThreshold;
    private final Map<String, CopyOnWriteArrayList<String>> userTimelines = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> followersByAuthor = new ConcurrentHashMap<>();
    private final Map<String, List<String>> postsByAuthor = new ConcurrentHashMap<>();

    public TimelineService(@Value("${systemdesign.feed.push-follower-threshold}") int pushFollowerThreshold) {
        this.pushFollowerThreshold = pushFollowerThreshold;
    }

    public void follow(String followerId, String authorId) {
        followersByAuthor.computeIfAbsent(authorId, key -> ConcurrentHashMap.newKeySet()).add(followerId);
    }

    public void publishPost(String authorId, String postId) {
        postsByAuthor.computeIfAbsent(authorId, key -> new CopyOnWriteArrayList<>()).add(postId);
        int followerCount = followersByAuthor.getOrDefault(authorId, Set.of()).size();
        if (followerCount <= pushFollowerThreshold) {
            for (String followerId : followersByAuthor.getOrDefault(authorId, Set.of())) {
                userTimelines.computeIfAbsent(followerId, key -> new CopyOnWriteArrayList<>()).add(postId);
            }
        }
        userTimelines.computeIfAbsent(authorId, key -> new CopyOnWriteArrayList<>()).add(postId);
    }

    public List<String> getTimeline(String userId) {
        List<String> timeline = new ArrayList<>(userTimelines.getOrDefault(userId, new CopyOnWriteArrayList<>()));
        for (Map.Entry<String, Set<String>> entry : followersByAuthor.entrySet()) {
            String authorId = entry.getKey();
            if (entry.getValue().contains(userId) && entry.getValue().size() > pushFollowerThreshold) {
                timeline.addAll(postsByAuthor.getOrDefault(authorId, List.of()));
            }
        }
        Collections.reverse(timeline);
        return timeline;
    }
}
