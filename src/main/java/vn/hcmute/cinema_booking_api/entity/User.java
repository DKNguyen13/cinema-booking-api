package vn.hcmute.cinema_booking_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 500)
    @NotNull(message = "Mật khẩu không được để trống")
    private String psw;

    @Column(nullable = false, length = 40)
    @NotNull(message = "Không được để trong tên")
    @Length(min = 6, max = 40)
    private String fullName;

    @Column(nullable = false, unique = true, length = 10)
    @NotNull(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\d{10})$", message = "Số điện thoại phải có 10 chữ số")
    private String phone;

    @Column(length = 70, nullable = false)
    @NotNull(message = "Địa chỉ không được bỏ trống")
    private String addr;

    @Column(nullable = false)
    @Min(0)
    @NotNull
    private int point;

    @Column(nullable = true)
    private String urlImage;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(nullable = false, name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Ticket> tickets;
}
