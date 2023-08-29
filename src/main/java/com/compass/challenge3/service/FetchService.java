package com.compass.challenge3.service;

import com.compass.challenge3.client.JSONParseClientAsync;
import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class FetchService {

    private final PostService postService;
    private final HistoryService historyService;
    private final CommentService commentService;
    private final JSONParseClientAsync proxy;

    @Autowired
    public FetchService(PostService postService,
                        HistoryService historyService,
                        CommentService commentService, JSONParseClientAsync proxy) {
        this.postService = postService;
        this.historyService = historyService;
        this.commentService = commentService;
        this.proxy = proxy;
    }

    public Post fetchPost(Long postId, Post post,
                                List<History> history){

        PostRecord postRecord;
        try {
            history.add(new History(0L, new Date(), "POST_FIND", null));
            postRecord = proxy.retrievePostAsync(postId).get();
            history.add(new History(0L, new Date(), "POST_OK", null));
        }
        catch(RetryableException | InterruptedException | ExecutionException e){
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

        return postService.recordToPost(postId, postRecord);
    }

    public List<Comment> fetchComments(Long postId, Post post,
                                             List<History> history) {

        List<CommentRecord> commentRecords;
        try {
            history.add(new History(0L, new Date(), "COMMENTS_FIND", null));
            commentRecords = proxy.retrieveCommentsAsync(postId).get();
            history.add(new History(0L, new Date(), "COMMENTS_OK", null));
        } catch (RetryableException | InterruptedException | ExecutionException e) {
            history.add(new History(0L, new Date(), "FAILED", null));
            history.add(new History(0L, new Date(), "DISABLED", null));
            List<History> savedHistory = historyService.saveAll(history);
            post.setHistory(savedHistory);
            postService.save(post);
            throw new RuntimeException("Communication Error");
        }

        List<Comment> comments = commentService.recordToComments(commentRecords);
        return commentService.saveAll(comments);
    }

}
