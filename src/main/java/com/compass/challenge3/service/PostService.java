package com.compass.challenge3.service;

import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import com.compass.challenge3.repository.CommentRepository;
import com.compass.challenge3.repository.HistoryRepository;
import com.compass.challenge3.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository,
                       HistoryRepository historyRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
    }

    public Post save(Post post) {

        Post savedPost = postRepository.save(post);
        List<Comment> comments = new ArrayList<>(savedPost.getComments());
        for(Comment comment : comments){
            comment.setPost(savedPost);
        }
        List<History> history = new ArrayList<>(savedPost.getHistory());
        for(History hist : history){
            hist.setPost(savedPost);
        }
        commentRepository.saveAll(comments);
        historyRepository.saveAll(history);
        return savedPost;
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

    public boolean existsById(Long id){
        return postRepository.existsById(id);
    }

    public Post findById(Long id){
        try {
            return postRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("There is no post with that id " +id+ " stored in database");
        }
    }

    public void updateHistory(Post post){
        Post savedPost = postRepository.save(post);
        List<History> history = new ArrayList<>(savedPost.getHistory());
        for(History hist : history){
            hist.setPost(savedPost);
        }
        historyRepository.saveAll(history);
    }

}
