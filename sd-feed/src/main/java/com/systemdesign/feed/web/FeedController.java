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

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final TimelineService timelineService;

    public FeedController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @PostMapping("/follow")
    public Map<String, String> follow(@RequestParam String followerId, @RequestParam String authorId) {
        timelineService.follow(followerId, authorId);
        return Map.of("status", "ok");
    }

    @PostMapping("/posts")
    public Map<String, String> publish(@RequestParam String authorId, @RequestParam String postId) {
        timelineService.publishPost(authorId, postId);
        return Map.of("status", "published");
    }

    @GetMapping("/{userId}")
    public List<String> timeline(@PathVariable String userId) {
        return timelineService.getTimeline(userId);
    }
}
