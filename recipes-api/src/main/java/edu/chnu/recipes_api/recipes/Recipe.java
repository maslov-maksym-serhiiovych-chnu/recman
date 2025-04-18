package edu.chnu.recipes_api.recipes;

import jakarta.persistence.*;

@Table(name = "recipes")
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String title;

    @Column(length = 1000000, unique = true, nullable = false)
    private String instructions;

    public Recipe() {
    }

    public Recipe(String title, String instructions) {
        this.title = title;
        this.instructions = instructions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructions() {
        return instructions;
    }
}
