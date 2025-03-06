package medico.PPE.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Enums.UserRole;
import org.springframework.data.annotation.Transient;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")

    private String email;

    @Column(name = "username")

    private String username;

    @Column(name = "password")
    @Transient
    private String password;

    @Column(name = "first_name")

    private String firstName;

    @Column(name = "last_name")

    private String lastName;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "gender")
    private String gender;


    @Enumerated(EnumType.STRING)
    private UserRole role=UserRole.valueOf("USER");

    @Column(name = "lastseen")
    @Transient
    private String lastseen;


}
