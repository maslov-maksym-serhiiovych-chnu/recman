package edu.chnu.recman_backend.recipes.models;

import edu.chnu.recman_backend.auth.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipes", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
@Check(constraints = "char_length(name) >= 3")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Recipe(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }
}
