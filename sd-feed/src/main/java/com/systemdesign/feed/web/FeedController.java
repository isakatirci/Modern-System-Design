package com.systemdesign.feed.web;

import com.systemdesign.feed.service.TimelineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Feed (timeline) işlemleri için REST API katmanı.
 * <p>
 * HTTP request'leri {@link TimelineService}'e yönlendirir; hybrid fanout
 * mantığı controller'da değil, domain service katmanında kalır.
 */
@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final TimelineService timelineService;

    /**
     * Timeline servisini inject eder.
     *
     * @param timelineService hybrid fanout timeline servisi
     */
    public FeedController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    /**
     * İki kullanıcı arasında takip ilişkisi kurar.
     *
     * @param followerId takip eden kullanıcı id
     * @param authorId   takip edilen yazar id
     * @return işlem durumu
     */
    @PostMapping("/follow")
    public Map<String, String> follow(@RequestParam String followerId, @RequestParam String authorId) {
        timelineService.follow(followerId, authorId);
        return Map.of("status", "ok");
    }

    /**
     * Yeni post yayınlar; servis follower sayısına göre push veya pull fanout seçer.
     *
     * @param authorId post sahibi yazar id
     * @param postId   yayınlanacak post id
     * @return yayın durumu
     */
    @PostMapping("/posts")
    public Map<String, String> publish(@RequestParam String authorId, @RequestParam String postId) {
        timelineService.publishPost(authorId, postId);
        return Map.of("status", "published");
    }

    /**
     * Kullanıcının birleştirilmiş timeline'ını döner.
     *
     * @param userId timeline isteyen kullanıcı id
     * @return post id listesi (en yeni önce)
     */
    @GetMapping("/{userId}")
    public List<String> timeline(@PathVariable String userId) {
        return timelineService.getTimeline(userId);
    }
}
