package com.compass.challenge3.controller;

import com.compass.challenge3.client.JSONParseClient;
import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import com.compass.challenge3.service.CommentService;
import com.compass.challenge3.service.HistoryService;
import com.compass.challenge3.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private JSONParseClient proxy;

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

        PostRecord postRecord;

            postRecord = proxy.retrievePost(postId);
            history.add(new History(0L, new Date(), "POST_OK", null));

        Post post = new Post(postId, postRecord.title(), postRecord.body(), new ArrayList<>(), new ArrayList<>());

        List<CommentRecord> commentRecords = proxy.retrieveComments(postId);
        history.add(new History(0L, new Date(), "COMMENTS_OK", null));
        List<Comment> comments = new ArrayList<>();
        for(CommentRecord c : commentRecords){
            comments.add(new Comment(c.id(), c.body(), null));
        }
        List<Comment> savedComments = commentService.saveAll(comments);
        post.setComments(savedComments);

        history.add(new History(0L, new Date(), "ENABLED", null));
        List<History> savedHistory = historyService.saveAll(history);
        post.setHistory(savedHistory);

        postService.save(post);

        return new ResponseEntity<>(postRecord, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Post>> queryPosts(){
        List<Post> allPosts = postService.findAll();
        return new ResponseEntity<>(allPosts, HttpStatus.OK);
    }

}
