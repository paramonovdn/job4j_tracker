package ru.job4j.tracker.model;

import lombok.*;

import javax.persistence.*;

import ru.job4j.toone.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private LocalDateTime created = LocalDateTime.now();

    public Item(String name) {
        this.name = name;
    }

    public Item(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(
            name = "participates",
            joinColumns = { @JoinColumn(name = "item_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private List<User> participates = new ArrayList<>();

    @Override
    public String toString() {
        return "Item("
                + "id=" + id
                + ", name=" + name
                + ", created=" + created
                + ')';
    }
}
