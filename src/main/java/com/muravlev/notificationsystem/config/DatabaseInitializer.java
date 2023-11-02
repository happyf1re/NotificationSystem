package com.muravlev.notificationsystem.config;

import com.muravlev.notificationsystem.user.User;
import com.muravlev.notificationsystem.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createSystemUserIfNeeded();
    }

    private void createSystemUserIfNeeded() {
        String systemUsername = "system";
        Optional<User> systemUser = userRepository.findByUserName(systemUsername);
        if (systemUser.isEmpty()) {
            User newUser = new User();
            newUser.setUserName(systemUsername);
            newUser.setPassword(passwordEncoder.encode("system"));
            newUser.setEmail("system@notificationsystem.com"); // Добавляем email для системного пользователя
            userRepository.save(newUser);
        }
    }
}
