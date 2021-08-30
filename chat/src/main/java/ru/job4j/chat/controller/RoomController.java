package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.room.Room;
import ru.job4j.chat.repository.RoomRepository;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomRepository rep;

    public RoomController(RoomRepository rep) {
        this.rep = rep;
    }

    @GetMapping("/")
    public List<Room> findAll() {
        List<Room> result = new LinkedList<>();
        rep.findAll().forEach(result::add);
        return result;
    }

    @GetMapping("/{id}")
    public Room findById(@PathVariable int id) {
        return rep.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Room is not found. Please, check id."
        ));
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@RequestBody Room room) {
        room = rep.save(room);
        return new ResponseEntity<>(
                room,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Room room) {
        room = rep.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        rep.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
