package com.compass.challenge3.controller;

import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import com.compass.challenge3.service.CommentService;
import com.compass.challenge3.service.FetchService;
import com.compass.challenge3.service.HistoryService;
import com.compass.challenge3.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private FetchService fetchService;

    @PostMapping("/{postId}")
    public ResponseEntity<PostRecord> processPost(@PathVariable Long postId){

        List<History> history = postService.initPost(postId);
        Post post = fetchService.fetchPost(postId, null, history);

        List<Comment> comments = fetchService.fetchComments(postId, post, history);
        post.setComments(comments);

        history.add(new History(0L, new Date(), "ENABLED", null));
        List<History> savedHistory = historyService.saveAll(history);
        post.setHistory(savedHistory);
        postService.save(post);

        return new ResponseEntity<>(new PostRecord(post.getTitle(), post.getBody()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> disablePost(@PathVariable Long postId){

        Post post = postService.findById(postId);
        postService.checkEnabled(post);

        History savedHist = historyService.save(new History(0L, new Date(), "DISABLED", null));
        post.getHistory().add(savedHist);
        postService.saveHistory(post);
        return new ResponseEntity<>("DISABLED", HttpStatus.OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> reprocessPost(@PathVariable Long postId){

        Post post = postService.findById(postId);
        List<History> history = postService.updateHistory(post);

        Post updatedPost = fetchService.fetchPost(postId, post, history);
        post.setBody(updatedPost.getBody());
        post.setTitle(updatedPost.getBody());

        List<Comment> comments = fetchService.fetchComments(postId, post, history);
        post.setComments(comments);

        history.add(new History(0L, new Date(), "ENABLED", null));
        List<History> savedHistory = historyService.saveAll(history);
        post.setHistory(savedHistory);
        postService.save(post);

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
