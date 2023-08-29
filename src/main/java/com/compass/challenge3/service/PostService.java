package com.compass.challenge3.service;

import com.compass.challenge3.dto.PostRecord;
import com.compass.challenge3.entity.Comment;
import com.compass.challenge3.entity.History;
import com.compass.challenge3.entity.Post;
import com.compass.challenge3.repository.CommentRepository;
import com.compass.challenge3.repository.HistoryRepository;
import com.compass.challenge3.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       CommentRepository commentRepository,
                       HistoryRepository historyRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
    }

    public Post recordToPost(Long postId, PostRecord postRecord){
         return new Post(postId, postRecord.title(), postRecord.body(), new ArrayList<>(), new ArrayList<>());
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

        if(id < 1 || id > 100){
            throw new IllegalArgumentException("Id is not in range [1, 100]");
        }

        try {
            return postRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("There is no post with that id " +id+ " stored in database");
        }
    }

    public Page<Post> findPostsByPage(@NotNull int pageNo, @NotNull int pageSize){
        Pageable postPage = PageRequest.of(pageNo, pageSize);
        return postRepository.findAll(postPage);
    }

    public List<History> initPost(Long postId){

        //Exception handler and initializing history of a specific post
        if(postId < 1 || postId > 100){
            throw new IllegalArgumentException("Id is not in range [1, 100]");
        }
        if(this.existsById(postId)){
            throw new IllegalArgumentException("This id has already been processed");
        }
        List<History> history = new ArrayList<>();
        history.add(new History(0L, new Date(), "CREATED", null));
        return history;
    }

    public void checkEnabled(Post post){
        int size = post.getHistory().size();
        if(!post.getHistory().get(size-1).getStatus().equals("ENABLED")){
            throw new IllegalArgumentException("This post is not enabled");
        }
    }

    public void saveHistory(Post post){
        Post savedPost = postRepository.save(post);
        List<History> history = new ArrayList<>(savedPost.getHistory());
        for(History hist : history){
            hist.setPost(savedPost);
        }
        historyRepository.saveAll(history);
    }

    public List<History> updateHistory(Post post){

        List<History> history = post.getHistory();
        int size = history.size();
        String status = history.get(size-1).getStatus();

        if(!(status.equals("ENABLED") || status.equals("DISABLED"))){
            throw new IllegalArgumentException("This post is not enabled for updates");
        }

        history.add(new History(0L, new Date(), "UPDATING", null));
        return history;
    }

}
