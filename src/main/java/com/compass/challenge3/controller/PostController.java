package com.compass.challenge3.controller;

import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import com.compass.challenge3.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private AsyncPostService asyncPostService;
    @Autowired
    private AsyncFetchService asyncFetchService;

    @PostMapping("/{postId}")
    public ResponseEntity<PostRecord> processPost(@PathVariable Long postId) throws ExecutionException, InterruptedException {

        CompletableFuture<List<History>> history = asyncPostService.initPost(postId);
        CompletableFuture<Post> post = asyncFetchService.fetchPost(postId, null, history.get());

        CompletableFuture<List<Comment>> comments = asyncFetchService.fetchComments(postId, post.get(), history.get());

        asyncPostService.saveContent(post.get(), comments.get(), history.get());

        return new ResponseEntity<>(asyncPostService.postToRecord(post.get()).get(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId) throws ExecutionException, InterruptedException {

        CompletableFuture<Post> post = asyncPostService.findById(postId);
        CompletableFuture<Void> disabled = asyncPostService.disablePost(post.get());

        CompletableFuture.allOf(post, disabled).join();

        return new ResponseEntity<>("DISABLED", HttpStatus.OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostRecord> reprocessPost(@PathVariable Long postId) throws ExecutionException, InterruptedException {

        CompletableFuture<Post> post = asyncPostService.findById(postId);
        CompletableFuture<List<History>> history = asyncPostService.updateHistory(post.get());

        CompletableFuture<Post> recentPost = asyncFetchService.fetchPost(postId, post.get(), history.get());
        asyncPostService.updateContent(post.get(), recentPost.get());

        CompletableFuture<List<Comment>> comments = asyncFetchService.fetchComments(postId, post.get(), history.get());
        asyncPostService.saveContent(post.get(), comments.get(), history.get());

        return new ResponseEntity<>(asyncPostService.postToRecord(post.get()).get(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Post>> queryPosts() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Post>> allPosts = asyncPostService.findAll();
        return new ResponseEntity<>(allPosts.get(), HttpStatus.OK);
    }

    @GetMapping(params = { "page", "size" })
    public ResponseEntity<List<Post>> queryPostsByPage(@RequestParam("page") int page,
                                                       @RequestParam("size") int size) throws ExecutionException, InterruptedException {
        CompletableFuture<Page<Post>> postsByPage = asyncPostService.findPostsByPage(page-1, size);
        return new ResponseEntity<>(postsByPage.get().getContent(), HttpStatus.OK);
    }

}
