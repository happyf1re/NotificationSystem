package com.muravlev.notificationsystem.externalsystem;

import com.muravlev.notificationsystem.channel.Channel;
import com.muravlev.notificationsystem.channel.ChannelRepository;
import com.muravlev.notificationsystem.post.Post;
import com.muravlev.notificationsystem.post.PostService;
import com.muravlev.notificationsystem.user.User;
import com.muravlev.notificationsystem.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/external")
public class ExternalSystemController {

    @Value("${api.key}")
    private String apiKey;

    private final PostService postService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ExternalSystemController(PostService postService, UserRepository userRepository, ChannelRepository channelRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @PostMapping("/logs")
    public ResponseEntity<?> receiveLogs(@RequestHeader("X-API-KEY") String providedApiKey, @RequestBody String logContent) {
        if (!apiKey.equals(providedApiKey)) {
            return new ResponseEntity<>("Invalid API key", HttpStatus.FORBIDDEN);
        }

        User systemUser = userRepository.findByUserName("system")
                .orElseThrow(() -> new EntityNotFoundException("System user not found"));

        Channel logChannel = channelRepository.findByChannelName("LogChannel")
                .orElseThrow(() -> new EntityNotFoundException("Log channel not found"));

        Post post = new Post();
        post.setAuthor(systemUser);
        post.setChannel(logChannel);
        post.setContent("SYSTEM: " + logContent + " at " + LocalDateTime.now());
        post.setCreationTime(LocalDateTime.now());

        postService.createPost(post, null); // Так как это системный вызов, JWT токен не требуется

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
