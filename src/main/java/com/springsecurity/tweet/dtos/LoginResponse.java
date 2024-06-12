package com.springsecurity.tweet.dtos;

public record LoginResponse(String accessToken, Long expiresIn) {
}
