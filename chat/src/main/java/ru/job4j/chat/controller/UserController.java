package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.role.Role;
import ru.job4j.chat.domain.user.User;
import ru.job4j.chat.domain.user.UserResponseEntity;
import ru.job4j.chat.repository.UserRepository;
import ru.job4j.chat.repository.UserStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UserController.class.getSimpleName());
    private static final String ROLE_API_ID = "http://localhost:8080/role/{id}";

    private final UserRepository rep;
    private final RestTemplate rest;
    private BCryptPasswordEncoder encoder;
    private UserStore users;

    private final ObjectMapper objectMapper;

    public UserController(UserRepository rep, RestTemplate rest, BCryptPasswordEncoder encoder,
                          UserStore users, ObjectMapper objectMapper) {
        this.rep = rep;
        this.rest = rest;
        this.encoder = encoder;
        this.users = users;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/all")
    public List<User> findAll() {
        return users.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable int id) {
        return rep.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User is not found. Please, check id."
                ));
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
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password length must be greater than 6");
        }
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

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}
