package com.springsecurity.tweet.services;

import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.dtos.FeedDto;
import com.springsecurity.tweet.dtos.FeedItemDto;
import com.springsecurity.tweet.dtos.TweetRequest;
import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.Tweet;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.TweetRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TweetServiceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Long IDTWEET = 1L;
    private static final String USERNAMEBASIC = "jean";
    private static final String EMAIL = "sandesjean@gmail.com";
    private static final String CONTENT = "testando uma nova menssagem no tweet!";
    private static final String PASSWORD = "123";
    private static final Role ROLEADMIN = new Role();
    private static final String PASSWORDENCODER = "passwordEncoder";
    private static final Instant CREATIONTIMESTAMP = Instant.now();
    private static final int PAGE = 0 ;
    private static final int PAGESIZE = 10;
    private static final int TOTALPAG0E = 0 ;
    private static final long TOTALELEMENTS = 1L;
    private static final UUID ID2 = UUID.randomUUID();
    @InjectMocks
    private TweetService tweetService;
    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtAuthenticationToken token;

    private  Role  basicRole= new Role();
    private Tweet tweet;
    private Tweet tweet2;
    private UserModel userBasic;
    private UserModel userBasic2;
    private FeedItemDto feedItemDto;
    private TweetRequest tweetRequest = new TweetRequest(CONTENT);
    private List<FeedItemDto> listFeed;
    private List<Tweet> tweets;
    Page<Tweet> tweetPage;
    @BeforeEach
    void setUp() {
       openMocks(this);
       startContent();
        System.out.println(tweet.getTweetId());
    }


    @Test
    void whenSaveThenReturnCreatedTweet() {

        when(token.getName()).thenReturn(ID.toString());
        when(userRepository.findById(UUID.fromString(ID.toString()))).thenReturn(Optional.of(userBasic));
        when(tweetRepository.save(any())).thenReturn(tweet);
        tweetService.save(tweetRequest,token);

        verify(userRepository , times(1)).findById(UUID.fromString(token.getName()));
        verify(tweetRepository, times(1)).save(argThat(tweet -> tweet.getContent().equals(CONTENT)));
    }

    @Test
    void whenDeleteThenReturnDeletedTweet() throws ForbiddenException {
        when(token.getName()).thenReturn(ID.toString());
        when(userRepository.findById(UUID.fromString(ID.toString()))).thenReturn(Optional.of(userBasic));
        when(tweetRepository.findById(IDTWEET)).thenReturn(Optional.of(tweet));

        tweetService.delete(IDTWEET, token);

        verify(tweetRepository,times(1)).deleteById(IDTWEET);
    }

    @Test
    void whenDeleteThenReturnForbiddenException(){
            when(token.getName()).thenReturn(ID.toString());
            when(userRepository.findById(UUID.fromString(ID.toString()))).thenReturn(Optional.of(userBasic));
            when(tweetRepository.findById(IDTWEET)).thenReturn(Optional.of(tweet2));

        assertThrows(ForbiddenException.class, () -> {
            tweetService.delete(IDTWEET, token);
        });
    }

    @Test
    void whenFindAllTweetsThenReturnPageOfFeedItemDto() {
        when(tweetRepository.findAll(PageRequest.of(PAGE, PAGESIZE, Sort.Direction.DESC, "creationTimestamp"))).thenReturn(tweetPage);

        Page<FeedItemDto> response =  tweetService.findALLTweets(PAGE,PAGESIZE);

        assertEquals(2, response.getTotalElements());
        assertEquals("testando uma nova menssagem no tweet!", response.getContent().get(0).content());
        assertEquals("jean", response.getContent().get(0).username());
        assertEquals("testando uma nova menssagem no tweet!", response.getContent().get(1).content());
        assertEquals("jean", response.getContent().get(1).username());

    }

    private void startContent() {
        basicRole.setName(Role.Values.BASIC.name());
        userBasic = new UserModel(USERNAMEBASIC,EMAIL, PASSWORDENCODER);
        userBasic.setUserId(ID);
        userBasic.setRoles(Set.of(basicRole));
        userBasic2 = new UserModel(USERNAMEBASIC,EMAIL, PASSWORDENCODER);
        userBasic2.setUserId(ID2);
        userBasic2.setRoles(Set.of(basicRole));
        tweet = new Tweet(IDTWEET,userBasic,CONTENT,CREATIONTIMESTAMP);
        tweet2 = new Tweet(IDTWEET,userBasic2,CONTENT,CREATIONTIMESTAMP);
        feedItemDto = new FeedItemDto(IDTWEET, CONTENT, USERNAMEBASIC);
        listFeed =  Arrays.asList(feedItemDto);
        tweets = Arrays.asList(tweet,tweet2);
        tweetPage = new PageImpl<>(tweets, PageRequest.of(PAGE, PAGESIZE, Sort.Direction.DESC, "creationTimestamp"), tweets.size());

    }
}


















