package medico.PPE.Repositories;

import medico.PPE.Models.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CreneauRepository extends JpaRepository<Creneau, Long> {
    
    // ✅ Utiliser "Docteur" avec majuscule (nom de la propriété + Id)
    List<Creneau> findByDocteurId(Long doctorId);
    
    List<Creneau> findByDocteurIdAndDisponibleTrue(Long doctorId);
    
    List<Creneau> findByDocteurIdAndDate(Long doctorId, LocalDate date);
    
    List<Creneau> findByDocteurIdAndDateAndDisponibleTrue(Long doctorId, LocalDate date);
}