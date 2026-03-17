package medico.PPE.Repositories;

import medico.PPE.Models.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    //  Récupère les rendez-vous avec le docteur en une seule requête (évite N+1)
    @Query("SELECT DISTINCT a FROM Appointment a LEFT JOIN FETCH a.doctor WHERE a.doctor.id = :doctorId")
    List<Appointment> findByDoctor_Id(@Param("doctorId") Long doctorId);
    
    //  Récupère TOUS les rendez-vous avec leurs docteurs en une seule requête
    @Query("SELECT DISTINCT a FROM Appointment a LEFT JOIN FETCH a.doctor")
    List<Appointment> findAllWithDoctor();
    
    //  Récupère les rendez-vous d'un patient par son email
    @Query("SELECT DISTINCT a FROM Appointment a LEFT JOIN FETCH a.doctor WHERE a.email = :email")
    List<Appointment> findByEmail(@Param("email") String email);

    boolean existsByCreneauId(Long creneauId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId")
    List<Appointment> findAppointmentsByPatientId(@Param("patientId") Long patientId);

}
