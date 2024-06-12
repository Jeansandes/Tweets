package com.springsecurity.tweet.dtos;

public record FeedItemDto(Long tweetId, String content, String username) {
}
