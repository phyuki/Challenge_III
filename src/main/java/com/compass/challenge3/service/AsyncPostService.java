package com.compass.challenge3.service;


import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncPostService {

    @Autowired
    private PostService postService;

    @Async
    public CompletableFuture<Post> recordToPost(Long postId, PostRecord postRecord){
        return CompletableFuture.completedFuture(postService.recordToPost(postId, postRecord));
    }

    @Async
    public CompletableFuture<PostRecord> postToRecord(Post post){
        return CompletableFuture.completedFuture(postService.postToRecord(post));
    }

    @Async
    public void saveContent(Post post, List<Comment> comments, List<History> history) {
        postService.saveContent(post, comments, history);
    }

    @Async
    public void updateContent(Post post, Post recentPost) {
        postService.updateContent(post, recentPost);
    }

    @Async
    public CompletableFuture<List<Post>> findAll(){
        return CompletableFuture.completedFuture(postService.findAll());
    }

    @Async
    public CompletableFuture<Boolean> existsById(Long id){
        return CompletableFuture.completedFuture(postService.existsById(id));
    }

    @Async
    public CompletableFuture<Post> findById(Long id){
        return CompletableFuture.completedFuture(postService.findById(id));
    }

    @Async
    public CompletableFuture<Page<Post>> findPostsByPage(@NotNull int pageNo, @NotNull int pageSize){
        return CompletableFuture.completedFuture(postService.findPostsByPage(pageNo, pageSize));
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<List<History>> initPost(Long postId) {
        return CompletableFuture.completedFuture(postService.initPost(postId));
    }

    @Async
    public CompletableFuture<Void> disablePost(Post post){
        postService.disablePost(post);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<List<History>> updateHistory(Post post){
        return CompletableFuture.completedFuture(postService.updateHistory(post));
    }

}
