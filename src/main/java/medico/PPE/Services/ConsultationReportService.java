package medico.PPE.Services;

import java.util.Optional;
import medico.PPE.Models.Appointment;
import medico.PPE.Models.ConsultationReport;
import medico.PPE.Repositories.AppointmentRepository;
import medico.PPE.Repositories.ConsultationReportRepository;
import medico.PPE.dtos.ConsultationReportDto;
import medico.PPE.dtos.ConsultationReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultationReportService {

    private final ConsultationReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public ConsultationReportService(
            ConsultationReportRepository reportRepository,
            AppointmentRepository appointmentRepository) {
        this.reportRepository = reportRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ConsultationReportDto> getByAppointmentId(Long appointmentId) {
        return reportRepository.findByAppointment_Id(appointmentId).map(this::toDto);
    }

    @Transactional
    public ConsultationReportDto create(Long appointmentId, Long doctorId, ConsultationReportRequest request) {
        if (reportRepository.existsByAppointment_Id(appointmentId)) {
            throw new IllegalStateException("Rapport deja existant pour ce rendez-vous");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable"));

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new IllegalStateException("Rendez-vous invalide (docteur manquant)");
        }
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("Acces refuse");
        }

        ConsultationReport report = new ConsultationReport();
        report.setAppointment(appointment);
        report.setDoctor(appointment.getDoctor());
        report.setPatient(appointment.getPatient());
        report.setTitle(request.getTitle());
        report.setContent(request.getContent());
        report.setDiagnosis(request.getDiagnosis());
        report.setTreatment(request.getTreatment());
        report.setPrescription(request.getPrescription());

        return toDto(reportRepository.save(report));
    }

    @Transactional
    public ConsultationReportDto update(Long appointmentId, Long doctorId, ConsultationReportRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous introuvable"));

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new IllegalStateException("Rendez-vous invalide (docteur manquant)");
        }
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("Acces refuse");
        }

        ConsultationReport report = reportRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Rapport introuvable"));

        // Mise a jour partielle (null => on garde l'existant)
        if (request.getTitle() != null) report.setTitle(request.getTitle());
        if (request.getContent() != null) report.setContent(request.getContent());
        if (request.getDiagnosis() != null) report.setDiagnosis(request.getDiagnosis());
        if (request.getTreatment() != null) report.setTreatment(request.getTreatment());
        if (request.getPrescription() != null) report.setPrescription(request.getPrescription());

        // Securise le lien avec le RDV
        report.setAppointment(appointment);
        report.setDoctor(appointment.getDoctor());
        report.setPatient(appointment.getPatient());

        return toDto(reportRepository.save(report));
    }

    private ConsultationReportDto toDto(ConsultationReport report) {
        Long appointmentId = report.getAppointment() != null ? report.getAppointment().getId() : null;
        Long doctorId = report.getDoctor() != null ? report.getDoctor().getId() : null;
        Long patientId = report.getPatient() != null ? report.getPatient().getId() : null;

        return new ConsultationReportDto(
                report.getId(),
                appointmentId,
                doctorId,
                patientId,
                report.getTitle(),
                report.getContent(),
                report.getDiagnosis(),
                report.getTreatment(),
                report.getPrescription(),
                report.getCreatedAt(),
                report.getUpdatedAt());
    }
}
