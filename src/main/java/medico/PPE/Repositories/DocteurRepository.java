package medico.PPE.Repositories;

import medico.PPE.Models.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocteurRepository extends JpaRepository<Docteur, Long> {
    Optional<Docteur> FindBEmail(String email);
    boolean existsByEmail(String email);
}
