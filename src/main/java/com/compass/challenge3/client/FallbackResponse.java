package com.compass.challenge3.client;

import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FallbackResponse implements JSONParseClient{

    @Override
    public PostRecord retrievePost(Long postId) {
        throw new NoFallbackAvailableException("External source is down for a while", new RuntimeException());
    }

    @Override
    public List<CommentRecord> retrieveComments(Long postId) {
        throw new NoFallbackAvailableException("External source is down for a while", new RuntimeException());
    }
}
