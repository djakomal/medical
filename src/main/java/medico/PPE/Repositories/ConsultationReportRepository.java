package medico.PPE.Repositories;

import java.util.Optional;
import medico.PPE.Models.ConsultationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationReportRepository extends JpaRepository<ConsultationReport, Long> {

    Optional<ConsultationReport> findByAppointment_Id(Long appointmentId);

    boolean existsByAppointment_Id(Long appointmentId);
}

