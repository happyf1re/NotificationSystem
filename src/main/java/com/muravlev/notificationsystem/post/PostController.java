package com.muravlev.notificationsystem.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            @RequestHeader("Authorization") String jwtToken
    ) {
        Post newPost = postService.createPost(post, jwtToken.replace("Bearer ", ""));
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Integer id) {
        return postService.getPostById(id);
    }

    @GetMapping("/channel/{channelId}")
    public List<Post> getPostsByChannelId(@PathVariable Integer channelId) {
        return postService.findPostsByChannel(channelId);
    }

    @DeleteMapping("/{id}")
    public void deletePostById(@PathVariable Integer id) {
        postService.deletePost(id);
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Integer id) {
        return postService.existsById(id);
    }
}
