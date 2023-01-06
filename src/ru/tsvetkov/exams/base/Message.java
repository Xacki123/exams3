package ru.tsvetkov.exams.base;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String name;
    private String text;
    private LocalDateTime date;

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDateTime() {
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
