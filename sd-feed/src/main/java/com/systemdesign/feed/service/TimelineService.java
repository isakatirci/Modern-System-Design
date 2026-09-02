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

/**
 * Kullanıcı timeline'ını yöneten servis; hybrid fanout stratejisini uygular.
 * <p>
 * Sistem tasarımı kavramı: Twitter/Facebook tarzı feed'lerde her post'u tüm
 * takipçilere yazmak (pure push) celebrity hesaplarda maliyetli olur.
 * Bu servis follower eşiğine göre iki mod arasında geçiş yapar:
 * <ul>
 *   <li><strong>Push fanout</strong> — az takipçili yazarlar için post, publish anında
 *       her takipçinin timeline cache'ine yazılır (hızlı okuma).</li>
 *   <li><strong>Pull fanout</strong> — eşiği aşan yazarlar için post sadece yazarın
 *       kendi listesinde tutulur; okuma anında takipçi timeline'ına merge edilir.</li>
 * </ul>
 */
@Service
public class TimelineService {

    private final int pushFollowerThreshold;
    /** Push modunda takipçi başına önceden doldurulmuş timeline cache. */
    private final Map<String, CopyOnWriteArrayList<String>> userTimelines = new ConcurrentHashMap<>();
    /** Yazar → takipçi kümesi; follower sayısı push/pull kararını belirler. */
    private final Map<String, Set<String>> followersByAuthor = new ConcurrentHashMap<>();
    /** Pull modundaki yazarların post listesi; okuma anında merge edilir. */
    private final Map<String, List<String>> postsByAuthor = new ConcurrentHashMap<>();

    /**
     * Eşik değerini config'den alarak servisi oluşturur.
     *
     * @param pushFollowerThreshold bu sayının altındaki yazarlar push fanout kullanır
     */
    public TimelineService(@Value("${systemdesign.feed.push-follower-threshold}") int pushFollowerThreshold) {
        this.pushFollowerThreshold = pushFollowerThreshold;
    }

    /**
     * Bir kullanıcının başka bir yazarı takip etmesini kaydeder.
     *
     * @param followerId takip eden kullanıcı id
     * @param authorId   takip edilen yazar id
     */
    public void follow(String followerId, String authorId) {
        followersByAuthor.computeIfAbsent(authorId, key -> ConcurrentHashMap.newKeySet()).add(followerId);
    }

    /**
     * Yeni post yayınlar; follower sayısına göre push veya pull fanout uygular.
     *
     * @param authorId post sahibi yazar id
     * @param postId   yayınlanan post id
     */
    public void publishPost(String authorId, String postId) {
        postsByAuthor.computeIfAbsent(authorId, key -> new CopyOnWriteArrayList<>()).add(postId);
        int followerCount = followersByAuthor.getOrDefault(authorId, Set.of()).size();
        // Push fanout: takipçi sayısı eşiğin altındaysa post'u her takipçinin cache'ine yaz
        if (followerCount <= pushFollowerThreshold) {
            for (String followerId : followersByAuthor.getOrDefault(authorId, Set.of())) {
                userTimelines.computeIfAbsent(followerId, key -> new CopyOnWriteArrayList<>()).add(postId);
            }
        }
        // Pull fanout: eşiği aşan yazarlar için post sadece yazarın kendi timeline'ına eklenir;
        // takipçiler okuma anında postsByAuthor üzerinden merge eder
        userTimelines.computeIfAbsent(authorId, key -> new CopyOnWriteArrayList<>()).add(postId);
    }

    /**
     * Kullanıcının timeline'ını döner; push cache ile pull merge sonucunu birleştirir.
     *
     * @param userId timeline isteyen kullanıcı id
     * @return en yeni post önce olacak şekilde sıralanmış post id listesi
     */
    public List<String> getTimeline(String userId) {
        List<String> timeline = new ArrayList<>(userTimelines.getOrDefault(userId, new CopyOnWriteArrayList<>()));
        // Pull fanout: celebrity yazarların post'larını okuma anında timeline'a merge et
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
