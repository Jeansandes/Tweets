package com.springsecurity.tweet.services;

import com.springsecurity.tweet.dtos.FeedDto;
import com.springsecurity.tweet.dtos.FeedItemDto;
import com.springsecurity.tweet.dtos.TweetRequest;
import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.Exceptions.IdNotFoundException;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.Tweet;
import com.springsecurity.tweet.repositores.TweetRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.UUID;

@Service
public class TweetService {

    private TweetRepository tweetRepository;
    private UserRepository userRepository;

    public TweetService(TweetRepository tweetRepository,UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }


    public void save(TweetRequest dto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName())
        );

        var tweet = new Tweet();

        tweet.setUser(user.get());
        tweet.setContent(dto.content());

        tweetRepository.save(tweet);
    }

    public void delete(Long id, JwtAuthenticationToken token) throws ForbiddenException {
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = tweetRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("Id not found!")
        );
        var isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));
        if(isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            tweetRepository.deleteById(id);
        }
        else {
            throw  new ForbiddenException("You don't have permission to access this resource");
        }
    }

    public Page<FeedItemDto> findALLTweets(int page, int pageSize) {
        var tweets = tweetRepository.findAll(
                        PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(tweet ->
                        new FeedItemDto(
                                tweet.getTweetId(),
                                tweet.getContent(),
                                tweet.getUser().getUsername())
                );
        return tweets;
    }
}


















