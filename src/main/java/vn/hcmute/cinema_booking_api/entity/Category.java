package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank(message = "Category name must not be blank")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String categoryName;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Movie> movies;
}