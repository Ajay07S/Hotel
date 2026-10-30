
package com.restaurant.repository;

import com.restaurant.models.MenuItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuRepository {

    private final List<MenuItem> menuItems = new ArrayList<>();

    public MenuRepository() {
        menuItems.add(
            new MenuItem(1, "Chicken Biryani", 180, "Main Course")
        );

        menuItems.add(
            new MenuItem(2, "Veg Fried Rice", 120, "Main Course")
        );

        menuItems.add(
            new MenuItem(3, "Fresh Juice", 50, "Drinks")
        );
    }

    public void save(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }
        if (findById(item.getId()).isPresent()) {
            throw new IllegalArgumentException("Menu item ID already exists");
        }
        menuItems.add(item);
    }

    public List<MenuItem> findAll() {
        return new ArrayList<>(menuItems);
    }

    public Optional<MenuItem> findById(int id) {
        return menuItems.stream()
            .filter(item -> item.getId() == id)
            .findFirst();
    }
}
