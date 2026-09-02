package com.systemdesign.feed.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TimelineServiceTest {

    @Autowired
    private TimelineService timelineService;

    @Test
    void pushFanoutForSmallFollowerCount() {
        timelineService.follow("u1", "author");
        timelineService.publishPost("author", "post-1");
        List<String> timeline = timelineService.getTimeline("u1");
        assertTrue(timeline.contains("post-1"));
    }
}
