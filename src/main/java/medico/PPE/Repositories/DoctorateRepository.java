package medico.PPE.Repositories;

import medico.PPE.Models.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorateRepository extends JpaRepository<Docteur, Long> {
    boolean existsByEmail(String email);

    Optional<Object> findByEmail(String email);
}
