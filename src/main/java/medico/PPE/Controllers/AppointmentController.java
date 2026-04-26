package medico.PPE.Controllers;

import medico.PPE.Models.Appointment;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Services.AppService;
import medico.PPE.dtos.AppointmentDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  // ✅ Un seul, @Controller retiré
@RequestMapping("/appointment")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173"}) // ✅ Angular + React
public class AppointmentController {

    @Autowired
    private AppService appService;

    @Autowired
    private DoctorateRepository doctorateRepository;

    // ── Helpers privés ───────────────────────────────────────────

    private Long getDocteurIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Docteur docteur = doctorateRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé: " + username));
        return docteur.getId();
    }

    private boolean isDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_DOCTOR".equals(a.getAuthority()));
    }

    // ── GET tous les rendez-vous ─────────────────────────────────

    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            if (isDoctor()) {
                Long doctorId = getDocteurIdFromToken();
                return ResponseEntity.ok(appService.getAppointmentByDoctor(doctorId));
            }
            return ResponseEntity.ok(appService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    // ── POST ajouter un rendez-vous ──────────────────────────────

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody AppointmentDto dto) {
        try {
            Appointment saved = appService.add(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'ajout: " + e.getMessage()));
        }
    }

    // ── GET par ID ───────────────────────────────────────────────

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAppById(@PathVariable Long id) {
        try {
            Appointment appointment = appService.getAppById(id);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Rendez-vous non trouvé"));
            }
            if (isDoctor()) {
                Long docteurId = getDocteurIdFromToken();
                if (appointment.getDoctor() != null &&
                        !appointment.getDoctor().getId().equals(docteurId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "Accès refusé"));
                }
            }
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    // ── DELETE supprimer ─────────────────────────────────────────

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (isDoctor()) {
                Appointment appointment = appService.getAppById(id);
                if (appointment != null && appointment.getDoctor() != null) {
                    Long docteurId = getDocteurIdFromToken();
                    if (!appointment.getDoctor().getId().equals(docteurId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "Suppression non autorisée"));
                    }
                }
            }
            appService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Rendez-vous supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    // ── GET rendez-vous d'un patient ─────────────────────────────

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
        Appointment appointment = appService.getAppointmentsByPatient(patientId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/patient/all/{patientId}")
    public ResponseEntity<?> getAllAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointment> appointments = appService.getAllAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    // ── PUT valider ──────────────────────────────────────────────

    @PutMapping("/{id}/validate")
    public ResponseEntity<Map<String, Object>> validateAppointment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (isDoctor()) {
                Appointment appointment = appService.getAppById(id);
                if (appointment == null) {
                    response.put("success", false);
                    response.put("message", "Rendez-vous non trouvé");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                Long docteurId = getDocteurIdFromToken();
                if (appointment.getDoctor() != null &&
                        !appointment.getDoctor().getId().equals(docteurId)) {
                    response.put("success", false);
                    response.put("message", "Validation non autorisée");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            Appointment validated = appService.validateAppointment(id);
            response.put("success", true);
            response.put("message", "Rendez-vous validé avec succès");
            response.put("appointment", validated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ── PUT rejeter ──────────────────────────────────────────────

    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectAppointment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (isDoctor()) {
                Appointment appointment = appService.getAppById(id);
                Long docteurId = getDocteurIdFromToken();
                if (appointment != null && appointment.getDoctor() != null &&
                        !appointment.getDoctor().getId().equals(docteurId)) {
                    response.put("success", false);
                    response.put("message", "Rejet non autorisé");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            Appointment rejected = appService.rejectAppointment(id);
            response.put("success", true);
            response.put("message", "Rendez-vous rejeté avec succès");
            response.put("appointment", rejected);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ── PUT démarrer ─────────────────────────────────────────────

    @PutMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startAppointment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (isDoctor()) {
                Appointment appointment = appService.getAppById(id);
                Long docteurId = getDocteurIdFromToken();
                if (appointment != null && appointment.getDoctor() != null &&
                        !appointment.getDoctor().getId().equals(docteurId)) {
                    response.put("success", false);
                    response.put("message", "Démarrage non autorisé");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            Appointment started = appService.startAppointment(id);
            response.put("success", true);
            response.put("message", "Rendez-vous débuté avec succès");
            response.put("joinUrl", started.getZoomJoinUrl());
            response.put("appointment", started);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ── PUT mettre à jour (Zoom + statut) ────────────────────────

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDto dto) {
        try {
            Appointment existing = appService.getAppById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Rendez-vous non trouvé avec ID: " + id));
            }

            // Mise à jour partielle — seuls les champs non null sont modifiés
            if (dto.getStatus() != null)        existing.setStatus(dto.getStatus());
            if (dto.getMeetingUrl() != null)    existing.setZoomJoinUrl(dto.getMeetingUrl());
            if (dto.getZoomMeetingId() != null) existing.setZoomMeetingId(dto.getZoomMeetingId());
            if (dto.getZoomStartUrl() != null)  existing.setZoomStartUrl(dto.getZoomStartUrl());
            if (dto.getZoomPassword() != null)  existing.setZoomPassword(dto.getZoomPassword());

            Appointment updated = appService.update(existing);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur mise à jour: " + e.getMessage()));
        }
    }

@GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appService.getAppointmentByDoctor(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
}
// Dans AppointmentController.java, ajoutez ces endpoints
@GetMapping("/{id}/documents")
public ResponseEntity<?> getDocuments(@PathVariable Long id) {
    try {
        Appointment appointment = appService.getAppById(id);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Rendez-vous non trouvé"));
        }
        return ResponseEntity.ok(Map.of(
            "medicalDocuments", appointment.getMedicalDocuments()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }
}

@PutMapping("/{id}/documents")
public ResponseEntity<?> updateDocuments(@PathVariable Long id, @RequestBody Map<String, String> body) {
    try {
        String medicalDocuments = body.get("medicalDocuments");
        Appointment updated = appService.updateMedicalDocuments(id, medicalDocuments);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Documents mis à jour",
            "medicalDocuments", updated.getMedicalDocuments()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", e.getMessage()));
    }
}

// Dans AppointmentController.java

// Récupérer les documents d'un rendez-vous
@GetMapping("/{id}/medical-documents")
public ResponseEntity<?> getMedicalDocuments(@PathVariable Long id) {
    try {
        Appointment appointment = appService.getAppById(id);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Rendez-vous non trouvé"));
        }
        
        String documents = appointment.getMedicalDocuments();
        if (documents == null || documents.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "medicalDocuments", new String[0],
                "count", 0
            ));
        }
        
        // Si les documents sont stockés en JSON
        // List<String> docList = new ObjectMapper().readValue(documents, new TypeReference<List<String>>() {});
        
        // Ou si c'est une simple chaîne séparée par des virgules
        String[] docArray = documents.split(",");
        
        return ResponseEntity.ok(Map.of(
            "medicalDocuments", docArray,
            "count", docArray.length
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }
}

// Ajouter/modifier les documents d'un rendez-vous
@PutMapping("/{id}/medical-documents")
public ResponseEntity<?> updateMedicalDocuments(@PathVariable Long id, @RequestBody Map<String, String> body) {
    try {
        String medicalDocuments = body.get("medicalDocuments");
        Appointment updated = appService.updateMedicalDocuments(id, medicalDocuments);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Documents mis à jour",
            "medicalDocuments", updated.getMedicalDocuments()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", e.getMessage()));
    }
}

// Récupérer tous les documents d'un patient (regroupés par rendez-vous)
@GetMapping("/patient/{patientId}/all-documents")
public ResponseEntity<?> getAllPatientDocuments(@PathVariable Long patientId) {
    try {
        List<Appointment> appointments = appService.getAllAppointmentsByPatient(patientId);
        
        List<Map<String, Object>> allDocuments = new ArrayList<>();
        
        for (Appointment apt : appointments) {
            if (apt.getMedicalDocuments() != null && !apt.getMedicalDocuments().isEmpty()) {
                Map<String, Object> docInfo = new HashMap<>();
                docInfo.put("appointmentId", apt.getId());
                docInfo.put("appointmentDate", apt.getPreferredDate());
                docInfo.put("documents", apt.getMedicalDocuments().split(","));
                allDocuments.add(docInfo);
            }
        }
        
        return ResponseEntity.ok(Map.of(
            "documents", allDocuments,
            "totalCount", allDocuments.stream().mapToInt(d -> ((String[])d.get("documents")).length).sum()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }
}
}