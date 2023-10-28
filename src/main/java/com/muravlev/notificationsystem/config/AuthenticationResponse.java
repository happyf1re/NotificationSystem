package com.muravlev.notificationsystem.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse {
    private String jwtToken;
    private Integer userId;

    public AuthenticationResponse(String jwtToken, Integer userId) {
        this.jwtToken = jwtToken;
        this.userId = userId;
    }
}
