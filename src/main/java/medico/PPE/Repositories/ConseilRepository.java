package medico.PPE.Repositories;

// ========================================
// 3. REPOSITORY
// ========================================

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medico.PPE.Models.Conseil;

import java.util.List;

@Repository
public interface ConseilRepository extends JpaRepository<Conseil, Long> {
    
    List<Conseil> findByPublieTrue();
    
    List<Conseil> findByCategorie(String categorie);
    
    List<Conseil> findByAuteur(String auteur);
    
    List<Conseil> findByTagsContaining(String tag);
    
    List<Conseil> findByTitreContainingIgnoreCase(String titre);
}