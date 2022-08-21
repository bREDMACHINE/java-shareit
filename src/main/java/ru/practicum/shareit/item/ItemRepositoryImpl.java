package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();
    private long id = 1;
    @Override
    public Item addItem(Item createItem) {
        items.add(createItem);
        createItem.setId(id);
        id++;
        return createItem;
    }

    @Override
    public Item updateItem(Item item) {
        for (Item updateItem : items) {
            if (updateItem.getId() == item.getId()) {
                updateItem.setName(item.getName());
                updateItem.setDescription(item.getDescription());
                updateItem.setAvailable(item.getAvailable());
                return updateItem;
            }
        }
        return null;
    }

    @Override
    public Optional<Item> getItem(long itemId) {
        for (Item item : items) {
            if (item.getId() == itemId) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Item> findAllItems(long userId) {
        return items.stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return items.stream()
                    .filter(Item -> (Item.getName().isBlank()
                            || Item.getDescription().isBlank())
                            && Item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        } else {
            return items.stream()
                    .filter(Item -> (Item.getName().toLowerCase().contains(text.toLowerCase())
                            || Item.getDescription().toLowerCase().contains(text.toLowerCase()))
                            && Item.getAvailable().equals(true))
                    .collect(Collectors.toList());
        }
    }
}
