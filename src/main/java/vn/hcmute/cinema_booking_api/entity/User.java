package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Full name must not be blank")
    @Size(min = 5, max = 40, message = "Full name must be between 5 and 40 characters")
    @Column(nullable = false, length = 40)
    private String fullName;

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(
            regexp = "^(\\d{10})$",
            message = "Phone number must contain exactly 10 digits"
    )
    @Column(nullable = false, unique = true, length = 10)
    private String phone;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 70, message = "Address must not exceed 70 characters")
    @Column(nullable = false, length = 70)
    private String address;

    @Min(value = 0, message = "Point must be greater than or equal to 0")
    @Builder.Default
    @Column(nullable = false)
    private Integer point = 0;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String imagePublicId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Ticket> tickets;
}