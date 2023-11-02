package com.muravlev.notificationsystem.invitation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvitationRequest {
    private Integer inviterId;
    private Integer inviteeId;
    private Integer channelId;
}
