package com.muravlev.notificationsystem.user;

import com.muravlev.notificationsystem.config.AuthenticationResponse;
import com.muravlev.notificationsystem.config.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No such user"));
        existingUser.setUserName(user.getUserName());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setEmail(user.getEmail());
        userRepository.save(existingUser);
        return existingUser;
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such user"));
        userRepository.delete(user);
    }

//    public boolean authenticate(String username, String password) {
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(EntityNotFoundException::new);
//        return user != null && passwordEncoder.matches(password, user.getPassword());
//    }

//    public ResponseEntity<?> authenticate(String username, String password) throws Exception {
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(EntityNotFoundException::new);
//
//        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            String token = jwtUtil.generateToken(user);
//            return ResponseEntity.ok(token);
//        } else {
//            throw new Exception("Incorrect username or password");
//        }
//    }

    public AuthenticationResponse authenticate(String username, String password) throws Exception {
        User user = userRepository.findByUserName(username)
                .orElseThrow(EntityNotFoundException::new);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user);
            return new AuthenticationResponse(token, user.getId());
        } else {
            throw new Exception("Incorrect username or password");
        }
    }
}
