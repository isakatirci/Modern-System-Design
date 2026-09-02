package com.systemdesign.shorturl.web.dto;

public record ShortUrlResponse(String shortKey, String shortUrl, String longUrl) {
}
