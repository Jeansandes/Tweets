package com.springsecurity.tweet.controllers;

import com.springsecurity.tweet.Exceptions.ForbiddenException;
import com.springsecurity.tweet.dtos.FeedItemDto;
import com.springsecurity.tweet.dtos.TweetRequest;
import com.springsecurity.tweet.models.Tweet;
import com.springsecurity.tweet.services.TweetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TweetControllerTest {

    @InjectMocks
    private TweetController tweetController;
    @Mock
    private TweetService tweetService;
    @Mock
    private JwtAuthenticationToken token;

    private TweetRequest tweetRequest;
    private Long id = 1L;
    private List<FeedItemDto> tweets;
    private Page<FeedItemDto> tweetPage;

    @BeforeEach
    void setUp() {
        openMocks(this);
        startContent();
    }

    private void startContent() {
        tweetRequest = new TweetRequest("tweet teste");
        tweets = List.of(new FeedItemDto(id,"tweet teste","jean")); // Adicione tweets conforme necessário
        tweetPage = new PageImpl<>(tweets, PageRequest.of(0, 10), tweets.size());
    }

    @Test
    void whenCreateTweetThenReturnOk() {
        doNothing().when(tweetService).save(tweetRequest, token);

        var response = tweetController.createTweet(tweetRequest,token);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(tweetService, times(1)).save(tweetRequest, token);
        verifyNoMoreInteractions(tweetService);
    }
    @Test
    void whenDeleteTweetThenReturnOk() throws ForbiddenException {
        doNothing().when(tweetService).delete(id, token);

        var response = tweetController.deleteTweet(id,token);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(tweetService, times(1)).delete(id, token);
        verifyNoMoreInteractions(tweetService);
    }
    @Test
    void whenGetAllTweetsThenReturnOk() {
        when(tweetService.findALLTweets(0,10)).thenReturn(tweetPage);

        var response = tweetController.getAllTweets(0,10);

        assertEquals(ResponseEntity.ok().build().getStatusCode(), response.getStatusCode());

        assertEquals(tweetPage.getContent().get(0).username(), response.getBody().feedItens().get(0).username());
    }
}