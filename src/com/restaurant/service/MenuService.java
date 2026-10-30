
package com.restaurant.service;

import com.restaurant.models.MenuItem;
import com.restaurant.repository.MenuRepository;

import java.util.List;
import java.util.Optional;

public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    public MenuItem getMenuItemById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Invalid menu item ID"
            );
        }

        Optional<MenuItem> item = menuRepository.findById(id);

        if (item.isEmpty()) {
            throw new IllegalArgumentException(
                "Menu item not found"
            );
        }

        return item.get();
    }

    public void updatePrice(int id, double newPrice) {

        if (!Double.isFinite(newPrice) || newPrice < 0) {
            throw new IllegalArgumentException(
                "Price must be a finite, non-negative amount"
            );
        }

        MenuItem item = getMenuItemById(id);
        item.setPrice(newPrice);
    }

    public void updateAvailability(int id, boolean available) {
        MenuItem item = getMenuItemById(id);
        item.setAvailable(available);
    }
}
