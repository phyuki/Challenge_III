package com.compass.challenge3.service;

import com.compass.challenge3.client.JSONParseClient;
import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FetchService {

    private final PostService postService;
    private final CommentService commentService;
    private final JSONParseClient proxy;

    @Autowired
    public FetchService(PostService postService,
                        CommentService commentService,
                        JSONParseClient proxy) {
        this.postService = postService;
        this.commentService = commentService;
        this.proxy = proxy;
    }

    public Post fetchPost(Long postId, Post post,
                                List<History> history){

        PostRecord postRecord;
        try {
            history.add(new History(0L, new Date(), "POST_FIND", null));
            postRecord = proxy.retrievePost(postId);
            history.add(new History(0L, new Date(), "POST_OK", null));
        }
        catch(RetryableException e){
            history.add(new History(0L, new Date(), "FAILED", null));
            if(post == null){
                post = new Post(postId, "", "", new ArrayList<>(), new ArrayList<>());
            }
            history.add(new History(0L, new Date(), "DISABLED", null));
            postService.saveContent(post, post.getComments(), history);
            throw new RuntimeException("Communication Error");
        }

        return postService.recordToPost(postId, postRecord);
    }

    public List<Comment> fetchComments(Long postId, Post post,
                                             List<History> history) {

        List<CommentRecord> commentRecords;
        try {
            history.add(new History(0L, new Date(), "COMMENTS_FIND", null));
            commentRecords = proxy.retrieveComments(postId);
            history.add(new History(0L, new Date(), "COMMENTS_OK", null));
        } catch (RetryableException e) {
            history.add(new History(0L, new Date(), "FAILED", null));
            history.add(new History(0L, new Date(), "DISABLED", null));
            postService.saveContent(post, post.getComments(), history);
            throw new RuntimeException("Communication Error");
        }

        List<Comment> comments = commentService.recordToComments(commentRecords);
        List<Comment> savedComments = commentService.saveAll(comments);
        post.setComments(comments);
        return savedComments;
    }

}
