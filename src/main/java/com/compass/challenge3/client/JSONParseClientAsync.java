package com.compass.challenge3.client;

import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class JSONParseClientAsync {

    @Autowired
    private JSONParseClient feignClient;

    @Async("asyncExecutor")
    public CompletableFuture<PostRecord> retrievePostAsync(Long postId) {
        return CompletableFuture.completedFuture(feignClient.retrievePost(postId));
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<CommentRecord>> retrieveCommentsAsync(Long postId) {
        return CompletableFuture.completedFuture(feignClient.retrieveComments(postId));
    }
}
