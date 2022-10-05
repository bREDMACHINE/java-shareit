package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    Long id;
    @Column(name = "item_name")
    String name;
    String description;
    @Column(name = "is_available")
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return id != null && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
