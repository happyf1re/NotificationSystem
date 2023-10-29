package com.muravlev.notificationsystem.channel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

//    @PostMapping("/create")
//    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
//        Channel newChannel = channelService.createChannel(channel);
//        return new ResponseEntity<>(newChannel, HttpStatus.CREATED);
//    }

    @PostMapping("/create")
    public ResponseEntity<Channel> createChannel(
            @RequestBody Channel channel,
            @RequestHeader("Authorization") String jwtToken
    ) {
        Channel newChannel = channelService.createChannel(channel, jwtToken.replace("Bearer ", ""));
        return new ResponseEntity<>(newChannel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Optional<Channel> getChannelById(@PathVariable Integer id) {
        return channelService.findChannelById(id);
    }

    @GetMapping("/all")
    public List<Channel> getAllChannels(@RequestHeader("Authorization") String jwtToken) {
        jwtToken = jwtToken.replace("Bearer ", "");
        return channelService.findAllChannels(jwtToken);
    }

    @GetMapping("/public")
    public List<Channel> getPublicChannels() {
        return channelService.findPublicChannels();
    }

    @GetMapping("/private")
    public List<Channel> getPrivateChannels() {
        return channelService.findPrivateChannels();
    }

    @PutMapping("/{id}")
    public Channel updateChannel(@RequestBody Channel channel) {
        return channelService.updateChannel(channel);
    }

    @DeleteMapping("/{id}")
    public void deleteChannel(@PathVariable Integer id) {
        channelService.deleteChannel(id);
    }


}
