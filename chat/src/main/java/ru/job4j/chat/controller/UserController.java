package ru.job4j.chat.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.domain.role.Role;
import ru.job4j.chat.domain.user.User;
import ru.job4j.chat.domain.user.UserResponseEntity;
import ru.job4j.chat.repository.UserRepository;
import ru.job4j.chat.repository.UserStore;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final String ROLE_API_ID = "http://localhost:8080/role/{id}";

    private final UserRepository rep;
    private final RestTemplate rest;
    private BCryptPasswordEncoder encoder;
    private UserStore users;

    public UserController(UserRepository rep, RestTemplate rest, BCryptPasswordEncoder encoder,
                          UserStore users) {
        this.rep = rep;
        this.rest = rest;
        this.encoder = encoder;
        this.users = users;
    }

    @GetMapping("/all")
    public List<User> findAll() {
        return users.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseEntity> findById(@PathVariable int id) {
        var user = rep.findById(id);
        return new ResponseEntity<>(
                getUserResponseEntity(user.orElse(new User())),
                user.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

/*    @PostMapping("/")
    public ResponseEntity<UserResponseEntity> create(@RequestBody User user) {
        user = rep.save(user);
        return new ResponseEntity<>(
                getUserResponseEntity(user),
                HttpStatus.CREATED
        );
    }*/

    @PostMapping("/sign-up")
    public void signUp(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        users.save(user);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody User user) {
        rep.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        rep.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private String getRoleByRoleId(int roleId) {
        return Objects.requireNonNull(rest.exchange(
                ROLE_API_ID,
                HttpMethod.GET,
                null,
                Role.class,
                roleId
        ).getBody()).getName();
    }

    private UserResponseEntity getUserResponseEntity(User user) {
        return new UserResponseEntity(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                getRoleByRoleId(user.getRoleId())
        );
    }
}
