package medico.PPE.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Models.Customer;
import medico.PPE.Models.OtpValidation;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpValidationDto {

    private Integer id;      
    private Instant creationOtp;
    private Instant expireOtp;
    private Instant activationOtp;
    private String code;
    private Customer user;

    public static OtpValidation toEntity(OtpValidationDto otpValidationDto) {
        if (otpValidationDto == null) return null;

        OtpValidation otpValidation = new OtpValidation();
        otpValidation.setId(otpValidationDto.getId());           
        otpValidation.setCreationOtp(otpValidationDto.getCreationOtp());
        otpValidation.setExpireOtp(otpValidationDto.getExpireOtp());
        otpValidation.setActivationOtp(otpValidationDto.getActivationOtp());
        otpValidation.setCode(otpValidationDto.getCode());
        otpValidation.setCustomer(otpValidationDto.getUser());

        return otpValidation;
    }

    public static OtpValidationDto fromEntity(OtpValidation otpValidation) {
        if (otpValidation == null) return null;

        return OtpValidationDto.builder()
                .id(otpValidation.getId())
                .creationOtp(otpValidation.getCreationOtp())
                .activationOtp(otpValidation.getActivationOtp())
                .expireOtp(otpValidation.getExpireOtp())
                .code(otpValidation.getCode())
                .user(otpValidation.getCustomer())               
                .build();
    }
}