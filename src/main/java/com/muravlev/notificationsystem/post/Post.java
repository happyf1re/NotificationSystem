package com.muravlev.notificationsystem.post;

import com.muravlev.notificationsystem.channel.Channel;
import com.muravlev.notificationsystem.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column
    private LocalDateTime creationTime;

    @ManyToOne
    private User author;

}