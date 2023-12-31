package com.muravlev.notificationsystem.channel;

import com.muravlev.notificationsystem.config.JwtUtil;
import com.muravlev.notificationsystem.user.User;
import com.muravlev.notificationsystem.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Channel createChannel(Channel channel, String jwtToken) {
        Integer userId = jwtUtil.getUserIdFromToken(jwtToken);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        channel.setCreator(author);
        if (channel.getChannelName() == null || channel.getChannelName().trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name is empty");
        }
        if (channel.getChannelType() == null) {
            throw new IllegalArgumentException("Channel type is empty");
        }
        if (channel.getCreator() == null || channel.getCreator().getId() == null) {
            throw new IllegalArgumentException("Channel creator is empty");
        }

        channel.setCreator(author);
        return channelRepository.save(channel);
    }

    @Transactional
    public Channel updateChannel(Channel channel) {
        Channel existingChannel = channelRepository.findById(channel.getId())
                .orElseThrow(() -> new EntityNotFoundException("No channel with such id"));
        if (channel.getChannelName() == null || channel.getChannelName().trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name is empty");
        }
        if (channel.getChannelType() == null) {
            throw new IllegalArgumentException("Channel type is empty");
        }
        existingChannel.setChannelName(channel.getChannelName());
        existingChannel.setChannelType(channel.getChannelType());
        channelRepository.save(existingChannel);
        return existingChannel;
    }

    @Transactional
    public void deleteChannel(Integer id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No channel with such id"));
        channelRepository.delete(channel);
        new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public boolean channelExistsById(Integer id) {
        return channelRepository.existsById(id);
    }

    public Optional<Channel> findChannelById(Integer id) {
        return channelRepository.findById(id);
    }

    public List<Channel> findAllChannels(String jwtToken) {
        // Извлекаем ID пользователя из JWT токена
        Integer currentUserId = jwtUtil.getUserIdFromToken(jwtToken);

        // Получаем все публичные каналы
        List<Channel> publicChannels = channelRepository.findAllByChannelType(ChannelType.PUBLIC);

        // Получаем приватные каналы, созданные текущим пользователем
        List<Channel> privateChannelsCreatedByUser = channelRepository.findAllByChannelTypeAndCreatorId(ChannelType.PRIVATE, currentUserId);

        // Получаем приватные каналы, на которые подписан текущий пользователь
        List<Channel> privateChannelsSubscribedByUser = channelRepository.findAllByChannelTypeAndSubscribersId(ChannelType.PRIVATE, currentUserId);

        // Объединяем списки
        List<Channel> allChannels = new ArrayList<>();
        allChannels.addAll(publicChannels);
        allChannels.addAll(privateChannelsCreatedByUser);
        allChannels.addAll(privateChannelsSubscribedByUser);

        return allChannels;
    }

    public List<Channel> findPublicChannels() {
        return channelRepository.findAllByChannelType(ChannelType.PUBLIC);
    }

    public List<Channel> findPrivateChannels() {
        return channelRepository.findAllByChannelType(ChannelType.PRIVATE);
    }

    public List<Channel> findSubscribedChannels(String jwtToken) {
        Integer currentUserId = jwtUtil.getUserIdFromToken(jwtToken);

        // Получаем каналы, на которые подписан текущий пользователь
        return channelRepository.findAllBySubscribersId(currentUserId);
    }

    @Transactional
    public void subscribe(Integer userId, Integer channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No such user"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("No such channel"));
        user.getSubscribedChannels().add(channel);
        if (channel.getSubscribers().contains(user)) {
            throw new IllegalArgumentException("User is already subscribed to this channel");
        }
        channel.getSubscribers().add(user);
        userRepository.save(user);
        channelRepository.save(channel);
    }

    @Transactional
    public void unsubscribe(Integer userId, Integer channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("No such channel"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No such user"));
        channel.getSubscribers().remove(user);
        user.getSubscribedChannels().remove(channel);
        userRepository.save(user);
        channelRepository.save(channel);
    }
}
