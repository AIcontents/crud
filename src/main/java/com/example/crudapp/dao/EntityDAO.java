package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;
import com.example.crudapp.model.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntityDAO {
    void add(Entity entity) throws ValidationException;
    void update(Entity entity) throws ValidationException;
    void delete(UUID id);
    Optional<Entity> get(UUID id);
    List<Entity> getAll();
}
