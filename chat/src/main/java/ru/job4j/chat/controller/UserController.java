package ru.job4j.chat.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.domain.role.Role;
import ru.job4j.chat.domain.user.User;
import ru.job4j.chat.domain.user.UserResponseEntity;
import ru.job4j.chat.repository.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class UserController {
    private static final String ROLE_API_ID = "http://localhost:8080/role/{id}";

    private final UserRepository rep;
    private final RestTemplate rest;

    public UserController(UserRepository rep, RestTemplate rest) {
        this.rep = rep;
        this.rest = rest;
    }

    @GetMapping("/")
    public List<UserResponseEntity> findAll() {
        List<UserResponseEntity> result = new LinkedList<>();
        rep.findAll().forEach(user -> result.add(getUserResponseEntity(user)));
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseEntity> findById(@PathVariable int id) {
        var user = rep.findById(id);
        return new ResponseEntity<>(
                getUserResponseEntity(user.orElse(new User())),
                user.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<UserResponseEntity> create(@RequestBody User user) {
        user = rep.save(user);
        return new ResponseEntity<>(
                getUserResponseEntity(user),
                HttpStatus.CREATED
        );
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
                user.getLogin(),
                user.getPassword(),
                getRoleByRoleId(user.getRoleId())
        );
    }
}
