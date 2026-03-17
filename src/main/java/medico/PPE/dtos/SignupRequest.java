package medico.PPE.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Models.Customer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    private Long id;        
    private String email;
    private String username;
    private String password;
    private String confirmpassword;
    private String gender;   

    public static SignupRequest fromEntity(Customer user) {
        if (user == null) return null;

        return SignupRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public static Customer toEntity(SignupRequest userDto) {
        if (userDto == null) return null;

        Customer user = new Customer();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());       // ✅ était "seEmail"
        user.setPassword(userDto.getPassword());
        return user;
    }
}