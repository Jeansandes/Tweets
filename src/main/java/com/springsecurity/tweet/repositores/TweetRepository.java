package com.springsecurity.tweet.repositores;

import com.springsecurity.tweet.models.Tweet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TweetRepository extends JpaRepository<Tweet,Long> {
    @Transactional
    void deleteAllByUser_UserId(UUID userId);
}
