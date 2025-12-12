package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntityDAO {
    void add(Entity entity) throws ValidationException;
    void update(Entity entity) throws ValidationException;
    void delete(UUID id);
    Optional<Entity> get(UUID id);
    List<Entity> getAll();
    List<Entity> search(String searchTerm, String sortBy, boolean sortAsc, String filterBy, int page, int pageSize);
    List<Entity> search(String searchTerm, String filterBy, boolean sortAsc, String sortBy, int page, int pageSize, LocalDateTime dateFrom, LocalDateTime dateTo);
    int getCount(String searchTerm, String filterBy);
    int getCount(String searchTerm, String filterBy, LocalDateTime dateFrom, LocalDateTime dateTo);
}
