package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.role.Role;
import ru.job4j.chat.domain.user.User;
import ru.job4j.chat.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleRepository rep;

    public RoleController(RoleRepository rep) {
        this.rep = rep;
    }

    @GetMapping("/")
    public List<Role> findAll() {
        return StreamSupport.stream(
                rep.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Role findById(@PathVariable int id) {
        return rep.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role is not found. Please, check id."
                ));
    }

    @PostMapping("/")
    public ResponseEntity<Role> create(@RequestBody Role role) {
        return new ResponseEntity<>(
                rep.save(role),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Role role) {
        rep.save(role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Role role = new Role();
        role.setId(id);
        rep.delete(role);
        return ResponseEntity.ok().build();
    }
}
