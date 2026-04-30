package medico.PPE.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import medico.PPE.Models.Publication;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
     @Query("SELECT p FROM Publication p WHERE p.publie = true")
    List<Publication> findByPublieTrue();
    List<Publication> findByDocteurId(Long docteurId);
 
}