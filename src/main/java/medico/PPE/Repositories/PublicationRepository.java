package medico.PPE.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import medico.PPE.Models.Publication;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    
    List<Publication> findByPublieTrue();
    
 
}