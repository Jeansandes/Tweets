package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.dtos.FeedDto;
import com.springsecurity.tweet.dtos.TweetRequest;
import com.springsecurity.tweet.models.Tweet;
import com.springsecurity.tweet.services.TweetService;
import org.modelmapper.internal.bytebuddy.TypeCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/tweet")
public class TweetController {
    private TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetRequest> createTweet(@RequestBody TweetRequest content,
                                                    JwtAuthenticationToken token){
        tweetService.save(content, token);
        return  ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<TweetRequest> deleteTweet(@PathVariable Long id,
                                                    JwtAuthenticationToken token) throws ForbiddenException {
        tweetService.delete(id,token);
        return ResponseEntity.ok().build();

    }
    @GetMapping("/feed")
    public ResponseEntity<FeedDto> getAllTweets(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        var tweets = tweetService.findALLTweets(page,pageSize);
       return ResponseEntity.ok(new FeedDto(tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getTotalElements()));

    }
}





























