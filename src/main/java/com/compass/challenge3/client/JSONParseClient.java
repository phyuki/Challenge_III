package com.compass.challenge3.client;

import com.compass.challenge3.dto.CommentRecord;
import com.compass.challenge3.dto.PostRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="posts", url="${external.source.url}")
public interface JSONParseClient {

    @GetMapping("${external.source.post.path}")
    PostRecord retrievePost(@PathVariable Long postId);

    @GetMapping("${external.source.comment.path}")
    List<CommentRecord> retrieveComments(@PathVariable Long postId);

}
