
package com.restaurant.service;

import com.restaurant.models.RestaurantTable;
import com.restaurant.repository.TableRepository;

import java.util.List;
import java.util.Optional;

public class TableService {

    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestaurantTable getTableById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Invalid table ID"
            );
        }

        Optional<RestaurantTable> table =
            tableRepository.findById(id);

        if (table.isEmpty()) {
            throw new IllegalArgumentException(
                "Table not found"
            );
        }

        return table.get();
    }

    public void updateTableStatus(
            int id,
            RestaurantTable.TableStatus status) {

        if (status == null) {
            throw new IllegalArgumentException(
                "Table status cannot be null"
            );
        }

        RestaurantTable table = getTableById(id);
        if (table.getStatus() == RestaurantTable.TableStatus.OCCUPIED
                && status != RestaurantTable.TableStatus.OCCUPIED) {
            throw new IllegalStateException(
                "An occupied table can only be released by completing or cancelling its order"
            );
        }
        table.setStatus(status);
    }
}
