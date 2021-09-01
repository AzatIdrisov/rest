package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Operation;
import ru.job4j.chat.domain.room.Room;
import ru.job4j.chat.repository.RoomRepository;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
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
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Room> create(@Valid @RequestBody Room room) {
        room = rep.save(room);
        return new ResponseEntity<>(
                room,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Room room) {
        room = rep.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        rep.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/patch")
    public Room patch(@Valid @RequestBody Room room) throws InvocationTargetException,
            IllegalAccessException {
        var currentRoom = rep.findById(room.getId());
        if (!currentRoom.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Patcher<Room> patcher = new Patcher();
        rep.save(patcher.update(currentRoom.get(), room));
        return currentRoom.get();
    }
}
