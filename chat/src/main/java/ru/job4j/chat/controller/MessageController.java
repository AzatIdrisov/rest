package ru.job4j.chat.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Operation;
import ru.job4j.chat.domain.message.Message;
import ru.job4j.chat.domain.message.MessageResponseEntity;
import ru.job4j.chat.domain.room.Room;
import ru.job4j.chat.domain.user.UserResponseEntity;
import ru.job4j.chat.repository.MessageRepository;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    private static final String USER_API_ID = "http://localhost:8080/user/{id}";
    private static final String ROOM_API_ID = "http://localhost:8080/room/{id}";

    private final MessageRepository rep;
    private final RestTemplate rest;

    public MessageController(MessageRepository rep, RestTemplate rest) {
        this.rep = rep;
        this.rest = rest;
    }

    @GetMapping("/")
    public List<MessageResponseEntity> findAll() {
        List<MessageResponseEntity> result = new LinkedList<>();
        rep.findAll().forEach(message -> result.add(transformToResponseEntity(message)));
        return result;
    }

    @GetMapping("/{id}")
    public Message findById(@PathVariable int id) {
        return rep.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message is not found. Please, check id."
                ));
    }

    @GetMapping("/room/{id}")
    public List<MessageResponseEntity> findByRoom(@PathVariable int id) {
        List<MessageResponseEntity> result = new LinkedList<>();
        rep.findAllByRoomId(id).forEach(msg -> result.add(transformToResponseEntity(msg)));
        return result;
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Message> create(@Valid @RequestBody Message message) {
        message = rep.save(message);
        return new ResponseEntity<>(
                message,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid  @RequestBody Message message) {
        rep.save(message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        rep.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/patch")
    public Message patch(@Valid @RequestBody Message message) throws InvocationTargetException,
            IllegalAccessException {
        var currentMessage = rep.findById(message.getId());
        if (!currentMessage.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Patcher<Message> patcher = new Patcher();
        rep.save(patcher.update(currentMessage.get(), message));
        return currentMessage.get();
    }

    private MessageResponseEntity transformToResponseEntity(Message message) {
        UserResponseEntity user = getUserById(message.getUserId());
        Room room = getRoomById(message.getRoomId());
        MessageResponseEntity entity = new MessageResponseEntity();
        entity.setMessage(message.getMessage());
        entity.setUser(user.getName());
        entity.setRoom(room.getName());
        return entity;
    }

    private UserResponseEntity getUserById(int id) {
        return rest.exchange(
                USER_API_ID,
                HttpMethod.GET,
                null,
                UserResponseEntity.class,
                id
        ).getBody();
    }

    private Room getRoomById(int id) {
        return rest.exchange(
                ROOM_API_ID,
                HttpMethod.GET,
                null,
                Room.class,
                id
        ).getBody();
    }
}
