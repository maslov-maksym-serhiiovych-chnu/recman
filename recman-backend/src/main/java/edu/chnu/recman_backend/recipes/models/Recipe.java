package edu.chnu.recman_backend.recipes.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "recipes")
@Check(constraints = "char_length(name) >= 3")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;
    
    public Recipe() {
    }

    public Recipe(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
