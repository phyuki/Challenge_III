package com.compass.challenge3.client;

import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="posts", url="https://jsonplaceholder.typicode.com")
public interface JSONParseClient {

    @GetMapping("/posts/{postId}")
    PostRecord retrievePost(@PathVariable Long postId);

    @GetMapping("posts/{postId}/comments")
    List<CommentRecord> retrieveComments(@PathVariable Long postId);

}
