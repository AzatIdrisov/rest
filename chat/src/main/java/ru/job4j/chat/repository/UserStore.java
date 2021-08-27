package ru.job4j.chat.repository;

import org.springframework.stereotype.Component;
import ru.job4j.chat.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStore {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public void save(User person) {
        users.put(person.getUsername(), person);
    }

    public User findByUsername(String username) {
        return users.get(username);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
