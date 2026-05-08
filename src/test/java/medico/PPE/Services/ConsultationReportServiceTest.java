package medico.PPE.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import medico.PPE.Models.Appointment;
import medico.PPE.Models.ConsultationReport;
import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.AppointmentRepository;
import medico.PPE.Repositories.ConsultationReportRepository;
import medico.PPE.dtos.ConsultationReportDto;
import medico.PPE.dtos.ConsultationReportRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultationReportServiceTest {

    @Mock
    private ConsultationReportRepository reportRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private ConsultationReportService service;

    @Test
    void create_whenReportAlreadyExists_throwsIllegalState() {
        when(reportRepository.existsByAppointment_Id(1L)).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.create(1L, 10L, new ConsultationReportRequest()));

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void create_whenDoctorMismatch_throwsSecurityException() {
        when(reportRepository.existsByAppointment_Id(1L)).thenReturn(false);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        Docteur doctor = new Docteur();
        doctor.setId(11L);
        appointment.setDoctor(doctor);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(
                SecurityException.class,
                () -> service.create(1L, 10L, new ConsultationReportRequest()));
    }

    @Test
    void create_success_savesAndReturnsDto() {
        when(reportRepository.existsByAppointment_Id(1L)).thenReturn(false);

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Docteur doctor = new Docteur();
        doctor.setId(10L);
        appointment.setDoctor(doctor);

        Customer patient = new Customer();
        patient.setId(20L);
        appointment.setPatient(patient);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        when(reportRepository.save(any(ConsultationReport.class))).thenAnswer(invocation -> {
            ConsultationReport report = invocation.getArgument(0);
            report.setId(100L);
            report.setCreatedAt(LocalDateTime.of(2026, 5, 8, 12, 0));
            report.setUpdatedAt(LocalDateTime.of(2026, 5, 8, 12, 0));
            return report;
        });

        ConsultationReportRequest request = new ConsultationReportRequest();
        request.setTitle("Titre");
        request.setContent("Contenu");

        ConsultationReportDto dto = service.create(1L, 10L, request);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(1L, dto.getAppointmentId());
        assertEquals(10L, dto.getDoctorId());
        assertEquals(20L, dto.getPatientId());
        assertEquals("Titre", dto.getTitle());
        assertEquals("Contenu", dto.getContent());

        verify(reportRepository).save(any(ConsultationReport.class));
    }
}

