package com.muravlev.notificationsystem.invitation;

import com.muravlev.notificationsystem.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    private final InvitationService invitationService;
    private final JwtUtil jwtUtil;

    public InvitationController(InvitationService invitationService, JwtUtil jwtUtil) {
        this.invitationService = invitationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUser(@RequestBody InvitationRequest invitationRequest, @RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtToken.replace("Bearer ", "");
        invitationService.invite(jwtToken, invitationRequest.getInviteeId(), invitationRequest.getChannelId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{invitationId}")
    public ResponseEntity<?> acceptInvitation(@PathVariable Integer invitationId, @RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtToken.replace("Bearer ", "");
        invitationService.acceptInvitation(jwtToken, invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline/{invitationId}")
    public ResponseEntity<?> declineInvitation(@PathVariable Integer invitationId, @RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtToken.replace("Bearer ", "");
        invitationService.declineInvitation(jwtToken, invitationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Invitation> getAllUserInvitations(@RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtToken.replace("Bearer ", "");
        Integer userId = jwtUtil.getUserIdFromToken(jwtToken);
        return invitationService.getAllInvitationsForUser(userId);
    }
}
