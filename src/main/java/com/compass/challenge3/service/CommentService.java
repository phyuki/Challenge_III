package com.compass.challenge3.service;

import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> saveAll(List<Comment> comments) {
        return commentRepository.saveAll(comments);
    }

}
