package ru.job4j.tracker.model;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class Item {
    private int id;
    private String name;
    private LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    public Item(String name) {
        this.name = name;
    }

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Timestamp getCreated() {
        return Timestamp.valueOf(created);
    }

    public void setCreated(Timestamp timestamp) {
        this.created = timestamp.toLocalDateTime();
    }

}
