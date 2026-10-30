
package com.restaurant.repository;

import com.restaurant.models.RestaurantTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableRepository {

    private final List<RestaurantTable> tables = new ArrayList<>();

    public TableRepository() {
        tables.add(new RestaurantTable(1, 2));
        tables.add(new RestaurantTable(2, 4));
        tables.add(new RestaurantTable(3, 6));
        tables.add(new RestaurantTable(4, 4));
    }

    public void save(RestaurantTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (findById(table.getId()).isPresent()) {
            throw new IllegalArgumentException("Table ID already exists");
        }
        tables.add(table);
    }

    public List<RestaurantTable> findAll() {
        return new ArrayList<>(tables);
    }

    public Optional<RestaurantTable> findById(int id) {
        return tables.stream()
            .filter(table -> table.getId() == id)
            .findFirst();
    }
}
