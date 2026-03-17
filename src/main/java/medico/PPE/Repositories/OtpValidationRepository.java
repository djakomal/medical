package medico.PPE.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Models.OtpValidation;

public interface OtpValidationRepository extends JpaRepository<OtpValidation, Integer> {
    Optional<OtpValidation> findByCode(String code);
    Optional<OtpValidation> findByCustomer(Customer customer);
     Optional<OtpValidation> findByDocteur(Docteur docteur); 
}