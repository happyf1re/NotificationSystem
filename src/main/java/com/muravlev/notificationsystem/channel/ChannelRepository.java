package com.muravlev.notificationsystem.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Integer> {
    List<Channel> findAllByChannelType(ChannelType type);

    List<Channel> findAllByChannelTypeAndCreatorId(ChannelType aPrivate, Integer currentUserId);

    List<Channel> findAllByChannelTypeAndSubscribersId(ChannelType aPrivate, Integer currentUserId);

    List<Channel> findAllBySubscribersId(Integer currentUserId);
}
