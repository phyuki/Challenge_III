package com.compass.challenge3.service;

import com.compass.challenge3.client.JSONParseClientAsync;
import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final JSONParseClientAsync proxy;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          JSONParseClientAsync proxy) {
        this.commentRepository = commentRepository;
        this.proxy = proxy;
    }

    public List<Comment> recordToComments(List<CommentRecord> commentRecords){
        List<Comment> comments = new ArrayList<>();
        for(CommentRecord c : commentRecords){
            comments.add(new Comment(c.id(), c.body(), null));
        }
        return comments;
    }

    public List<Comment> saveAll(List<Comment> comments) {
        return commentRepository.saveAll(comments);
    }

}
