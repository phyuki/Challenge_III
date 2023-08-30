package com.compass.challenge3.service;

import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFetchService {

    @Autowired
    private FetchService fetchService;

    @Async
    public CompletableFuture<Post> fetchPost(Long postId, Post post,
                                            List<History> history) {
        return CompletableFuture.completedFuture(fetchService.fetchPost(postId, post, history));
    }

    @Async
    public CompletableFuture<List<Comment>> fetchComments(Long postId, Post post,
                                       List<History> history) {
        return CompletableFuture.completedFuture(fetchService.fetchComments(postId, post, history));
    }

}
