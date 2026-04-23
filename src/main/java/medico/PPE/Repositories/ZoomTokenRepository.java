package medico.PPE.Repositories;

import medico.PPE.Models.ZoomToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoomTokenRepository extends JpaRepository<ZoomToken, Long> {
    
    // Récupérer le token le plus récent
    Optional<ZoomToken> findTopByOrderByIdDesc();
    
}
