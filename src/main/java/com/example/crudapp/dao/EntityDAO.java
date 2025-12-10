package com.example.crudapp.dao;

import com.example.crudapp.model.Entity;

import java.util.List;

public interface EntityDAO {

    void add(Entity entity);

    void update(Entity entity);

    void delete(int id);

    Entity get(int id);

    List<Entity> getAll(int page, int pageSize, String filter);
}
