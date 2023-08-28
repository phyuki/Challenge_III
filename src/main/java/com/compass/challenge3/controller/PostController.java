package com.compass.challenge3.controller;

import com.compass.challenge3.client.JSONParseClientAsync;
import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import com.compass.challenge3.service.CommentService;
import com.compass.challenge3.service.HistoryService;
import com.compass.challenge3.service.PostService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private JSONParseClientAsync proxy;

    public PostRecord fetchPosts(Long postId, Post post,
                                 List<History> history){
        PostRecord postRecord;
        try {
            history.add(new History(0L, new Date(), "POST_FIND", null));
            postRecord = proxy.retrievePostAsync(postId).get();
            history.add(new History(0L, new Date(), "POST_OK", null));
        }
        catch(FeignException | InterruptedException | ExecutionException e){
            history.add(new History(0L, new Date(), "FAILED", null));
            if(post == null){
                post = new Post(postId, "", "", new ArrayList<>(), new ArrayList<>());
            }
            history.add(new History(0L, new Date(), "DISABLED", null));
            List<History> savedHistory = historyService.saveAll(history);
            post.setHistory(savedHistory);
            postService.save(post);
            throw new RuntimeException("Communication Error");
        }
        return postRecord;
    }

    public Post fetchComments(Long postId, Post post,
                                             List<History> history){
        List<CommentRecord> commentRecords;

        try {
            history.add(new History(0L, new Date(), "COMMENTS_FIND", null));
            commentRecords = proxy.retrieveCommentsAsync(postId).get();
            history.add(new History(0L, new Date(), "COMMENTS_OK", null));
        } catch(FeignException | InterruptedException | ExecutionException e){
            history.add(new History(0L, new Date(), "FAILED", null));
            history.add(new History(0L, new Date(), "DISABLED", null));
            List<History> savedHistory = historyService.saveAll(history);
            post.setHistory(savedHistory);
            postService.save(post);
            throw new RuntimeException("Communication Error");
        }

        List<Comment> comments = new ArrayList<>();
        for(CommentRecord c : commentRecords){
            comments.add(new Comment(c.id(), c.body(), null));
        }

        List<Comment> savedComments = commentService.saveAll(comments);
        post.setComments(savedComments);

        history.add(new History(0L, new Date(), "ENABLED", null));
        List<History> savedHistory = historyService.saveAll(history);
        post.setHistory(savedHistory);

        return post;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<PostRecord> processPost(@PathVariable Long postId){

        if(postId < 1 || postId > 100){
            throw new EntityNotFoundException("Post does not exist with id: " + postId);
        }
        if(postService.existsById(postId)){
            throw new IllegalArgumentException("This id has already been processed");
        }
        List<History> history = new ArrayList<>();
        history.add(new History(0L, new Date(), "CREATED", null));

        PostRecord postRecord = fetchPosts(postId, null, history);

        Post post = new Post(postId, postRecord.title(), postRecord.body(), new ArrayList<>(), new ArrayList<>());
        Post processedPost = fetchComments(postId, post, history);
        postService.save(processedPost);

        return new ResponseEntity<>(postRecord, HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId){

        if(postId < 1 || postId > 100){
            throw new EntityNotFoundException("Post does not exist with id: " + postId);
        }

        Post post = postService.findById(postId);

        int size = post.getHistory().size();
        if(!post.getHistory().get(size-1).getStatus().equals("ENABLED")){
            throw new IllegalArgumentException("This post is not enabled");
        }

        History savedHist = historyService.save(new History(0L, new Date(), "DISABLED", null));
        post.getHistory().add(savedHist);
        postService.updateHistory(post);
        return new ResponseEntity<>("DISABLED", HttpStatus.OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId){

        if(postId < 1 || postId > 100){
            throw new EntityNotFoundException("Post does not exist with id: " + postId);
        }

        Post post = postService.findById(postId);
        List<History> history = post.getHistory();
        int size = history.size();
        String status = history.get(size-1).getStatus();

        if(!(status.equals("ENABLED") || status.equals("DISABLED"))){
            throw new IllegalArgumentException("This post is not enabled for updates");
        }

        history.add(new History(0L, new Date(), "UPDATING", null));

        PostRecord postRecord = fetchPosts(postId, post, history);
        post.setBody(postRecord.body());
        post.setTitle(postRecord.title());

        Post processedPost = fetchComments(postId, post, history);
        postService.save(processedPost);

        return new ResponseEntity<>("ENABLED", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Post>> queryPosts(){
        List<Post> allPosts = postService.findAll();
        return new ResponseEntity<>(allPosts, HttpStatus.OK);
    }

    @GetMapping(params = { "page", "size" })
    public ResponseEntity<List<Post>> queryPostsByPage(@RequestParam("page") int page,
                                                       @RequestParam("size") int size){
        Page<Post> postsByPage = postService.findPostsByPage(page-1, size);
        List<Post> allPostsInPage = postsByPage.getContent();
        return new ResponseEntity<>(allPostsInPage, HttpStatus.OK);
    }

}
