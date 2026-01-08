package medico.PPE.Repositories;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorateRepository extends JpaRepository<Docteur, Long> {


   
        Optional<Docteur> findByUsername(String username);
    Optional<Docteur> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
