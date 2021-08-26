package ru.job4j.chat.domain.message;

public class MessageResponseEntity {
    private String message;
    private String user;
    private String room;

    public MessageResponseEntity(String content, String user, String room) {
        this.message = content;
        this.user = user;
        this.room = room;
    }

    public MessageResponseEntity() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
