package com.muravlev.notificationsystem.invitation;

import com.muravlev.notificationsystem.channel.Channel;
import com.muravlev.notificationsystem.channel.ChannelRepository;
import com.muravlev.notificationsystem.channel.ChannelService;
import com.muravlev.notificationsystem.channel.ChannelType;
import com.muravlev.notificationsystem.config.JwtUtil;
import com.muravlev.notificationsystem.user.User;
import com.muravlev.notificationsystem.user.UserRepository;
import com.muravlev.notificationsystem.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class InvitationService {

    private final UserService userService;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final JwtUtil jwtUtil;

    public InvitationService(UserService userService, ChannelService channelService, ChannelRepository channelRepository, UserRepository userRepository, InvitationRepository invitationRepository, JwtUtil jwtUtil) {
        this.userService = userService;
        this.channelService = channelService;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void invite(String jwtToken, Integer inviteeId, Integer channelId) {
        Integer inviterId = jwtUtil.getUserIdFromToken(jwtToken);
        System.out.println("Inviter ID from JWT: " + inviterId);
        User inviter = userService.getUserById(inviterId)
                .orElseThrow(() -> new EntityNotFoundException("No such user (inviter) with ID: " + inviterId));

        System.out.println("Invitee ID: " + inviteeId);
        User invitee = userService.getUserById(inviteeId)
                .orElseThrow(() -> new EntityNotFoundException("No such user (invitee) with ID: " + inviteeId));

        System.out.println("Channel ID: " + channelId);
        Channel channel = channelService.findChannelById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("No such channel with ID: " + channelId));

        System.out.println(Arrays.toString(channel.getSubscribers().toArray()));
        System.out.println(channel.getId());

        if (channel.getChannelType() == ChannelType.PRIVATE && !channel.getCreator().equals(inviter)) {
            throw new IllegalArgumentException("Only the creator can invite users to a private channel");
        }

        if (!channel.getSubscribers().contains(inviter) && !channel.getCreator().equals(inviter) ) {
            System.out.println(Arrays.toString(channel.getSubscribers().toArray()));
            throw new IllegalArgumentException("Inviter must be a subscriber of the channel");
        }

        if (channel.getSubscribers().contains(invitee)) {
            throw new IllegalArgumentException("Invitee is already a subscriber of the channel");
        }

        Invitation invitation = new Invitation();
        invitation.setChannel(channel);
        invitation.setInviter(inviter);
        invitation.setInvitee(invitee);
        invitation.setStatus(InvitationStatus.INVITED);
        invitationRepository.save(invitation);
    }


    @Transactional
    public void acceptInvitation(String jwtToken, Integer invitationId) {
        Integer inviteeId = jwtUtil.getUserIdFromToken(jwtToken);
        User invitee = userService.getUserById(inviteeId)
                .orElseThrow(() -> new EntityNotFoundException("No such user (invitee)"));
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("No such invitation"));

        if (!invitation.getInvitee().equals(invitee)) {
            throw new IllegalArgumentException("Only the invitee can accept the invitation");
        }

        if (invitation.getStatus() != InvitationStatus.INVITED) {
            throw new IllegalArgumentException("Invitation has already been responded to");
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.getInvitee().getSubscribedChannels().add(invitation.getChannel());
        invitation.getChannel().getSubscribers().add(invitee);
        userRepository.save(invitee);
        channelRepository.save(invitation.getChannel());
        invitationRepository.save(invitation);
    }


    @Transactional
    public void declineInvitation(String jwtToken, Integer invitationId) {
        Integer inviteeId = jwtUtil.getUserIdFromToken(jwtToken);
        User invitee = userService.getUserById(inviteeId)
                .orElseThrow(() -> new EntityNotFoundException("No such user (invitee)"));
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("No such invitation"));

        if (!invitation.getInvitee().equals(invitee)) {
            throw new IllegalArgumentException("Only the invitee can decline the invitation");
        }

        if (invitation.getStatus() != InvitationStatus.INVITED) {
            throw new IllegalArgumentException("Invitation has already been responded to");
        }

        invitation.setStatus(InvitationStatus.DECLINED);
        invitationRepository.save(invitation);
    }

    public List<Invitation> getAllInvitationsForUser(Integer userId) {
        return invitationRepository.findAllByInviteeId(userId);
    }
}
