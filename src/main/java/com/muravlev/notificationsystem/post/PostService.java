package com.muravlev.notificationsystem.post;

import com.muravlev.notificationsystem.channel.Channel;
import com.muravlev.notificationsystem.channel.ChannelRepository;
import com.muravlev.notificationsystem.config.JwtUtil;
import com.muravlev.notificationsystem.user.User;
import com.muravlev.notificationsystem.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, UserRepository userRepository, ChannelRepository channelRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Post createPost(Post post, String jwtToken) {
        Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        // Поиск пользователя по ID
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + post.getAuthor().getId()));

        // Поиск канала по ID
        Channel channel = channelRepository.findById(post.getChannel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found with ID: " + post.getChannel().getId()));

        // Установка автора, канала и времени создания для поста
        post.setAuthor(author);
        post.setChannel(channel);
        post.setCreationTime(LocalDateTime.now());

        // Сохранение поста в базе данных
        return postRepository.save(post);
    }

    public Post getPostById(Integer id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such post"));
    }

    public List<Post> findPostsByChannel(Integer channelId) {
        return postRepository.findAllByChannelId(channelId);
    }

    public boolean existsById(Integer id) {
        return postRepository.existsById(id);
    }

    public void deletePost(Integer id) {
        postRepository.deleteById(id);
    }
}
