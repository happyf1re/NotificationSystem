package com.muravlev.notificationsystem.user;

import com.muravlev.notificationsystem.config.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    //    @PostMapping("/login")
//    public ResponseEntity<?> authenticate(@RequestBody User user) throws Exception {
//        if (userService.authenticate(user.getUserName(), user.getPassword())) {
//            return ResponseEntity.ok("Успешная аутентификация");
//        } else {
//            throw new Exception("Incorrect username or password");
//        }
//    }
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticate(@RequestBody User user) throws Exception {
//        return userService.authenticate(user.getUserName(), user.getPassword());
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody User user) throws Exception {
        return ResponseEntity.ok(userService.authenticate(user.getUserName(), user.getPassword()));
    }
}
