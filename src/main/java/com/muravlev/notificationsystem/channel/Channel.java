package com.muravlev.notificationsystem.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.muravlev.notificationsystem.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String channelName;

    @Enumerated
    private ChannelType channelType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "channel_subscribers",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> subscribers = new ArrayList<>();
}
