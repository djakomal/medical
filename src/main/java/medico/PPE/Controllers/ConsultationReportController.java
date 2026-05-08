package medico.PPE.Controllers;

import java.util.Map;
import java.util.Optional;
import medico.PPE.Models.Appointment;
import medico.PPE.Repositories.AppointmentRepository;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Services.ConsultationReportService;
import medico.PPE.Services.CustomUserDetails;
import medico.PPE.dtos.ConsultationReportDto;
import medico.PPE.dtos.ConsultationReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointment")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173"})
public class ConsultationReportController {

    @Autowired
    private ConsultationReportService reportService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorateRepository doctorateRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private boolean isDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_DOCTOR".equals(a.getAuthority()));
    }

    private Long getUserIdFromPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        return null;
    }

    private Long getCurrentUserId() {
        Long id = getUserIdFromPrincipal();
        if (id != null) return id;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String username = auth.getName();

        if (isDoctor()) {
            return doctorateRepository.findByUsername(username)
                    .map(d -> d.getId())
                    .orElse(null);
        }
        return customerRepository.findByUsername(username)
                .map(c -> c.getId())
                .orElse(null);
    }

    private boolean canAccessAppointment(Appointment appointment, Long currentUserId) {
        if (appointment == null || currentUserId == null) return false;
        if (isDoctor()) {
            return appointment.getDoctor() != null
                    && appointment.getDoctor().getId() != null
                    && appointment.getDoctor().getId().equals(currentUserId);
        }
        return appointment.getPatient() != null
                && appointment.getPatient().getId() != null
                && appointment.getPatient().getId().equals(currentUserId);
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentification requise"));
            }

            Appointment appointment = appointmentRepository.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Rendez-vous non trouve"));
            }

            if (!canAccessAppointment(appointment, currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Acces refuse"));
            }

            Optional<ConsultationReportDto> report = reportService.getByAppointmentId(id);
            return report.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "Rapport introuvable")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<?> createReport(@PathVariable Long id, @RequestBody ConsultationReportRequest request) {
        try {
            if (!isDoctor()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Acces refuse"));
            }

            Long doctorId = getCurrentUserId();
            if (doctorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentification requise"));
            }

            ConsultationReportDto created = reportService.create(id, doctorId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/report")
    public ResponseEntity<?> updateReport(@PathVariable Long id, @RequestBody ConsultationReportRequest request) {
        try {
            if (!isDoctor()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Acces refuse"));
            }

            Long doctorId = getCurrentUserId();
            if (doctorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentification requise"));
            }

            ConsultationReportDto updated = reportService.update(id, doctorId, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }
}

