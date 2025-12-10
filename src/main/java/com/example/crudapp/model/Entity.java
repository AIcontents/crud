package com.example.crudapp.model;

public class Entity {

    private int id;
    private String name;
    private String description;

    public Entity() {
    }

    public Entity(int id, String name, String description) {
        this.id = id;
        setName(name);
        setDescription(description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.length() < 3 || name.length() > 50) {
            throw new ValidationException("Name must be between 3 and 50 characters.");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new ValidationException("Description cannot be longer than 255 characters.");
        }
        this.description = description;
    }
}
