package medico.PPE.Models;


import lombok.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "OtpValidation")
public class OtpValidation extends AbstractEntity{

    @Column(name = "creationOtp")
    private Instant creationOtp;

    @Column(name = "expireOtp")
    private Instant expireOtp;

    @Column(name = "activationOtp")
    private Instant activationOtp;

    @Column(name = "code")
    private String code;

    @OneToOne(cascade = CascadeType.MERGE)  
    private Customer customer;

    @OneToOne(cascade = CascadeType.MERGE)
    private Docteur docteur;
}
