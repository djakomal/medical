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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.lang.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/appointment")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    @Autowired
    private AppService appService;
    
    @Autowired
    private DoctorateRepository doctorateRepository;

    //  Récupérer l'ID du docteur connecté
    private Long getDocteurIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Docteur docteur = doctorateRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        
        return docteur.getId();
    }
    
    //  Vérifier si l'utilisateur est un docteur
    private boolean isDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(a -> "ROLE_DOCTOR".equals(a.getAuthority()));
    }

    //  RÉCUPÉRER TOUS LES RENDEZ-VOUS (filtré par docteur si c'est un docteur)
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            System.out.println("📋 GET /appointment - Récupération des rendez-vous");
            
            if (isDoctor()) {
                Long doctorId = getDocteurIdFromToken();
                System.out.println("👨‍⚕️ Docteur ID: " + doctorId + " - Ses rendez-vous uniquement");
                
                List<Appointment> appointments = appService.getAppointmentByDoctor(doctorId); 
                System.out.println(" " + appointments.size() + " rendez-vous trouvés");
                
                return ResponseEntity.ok(appointments);
            } else {
                System.out.println("👤 User - Tous les rendez-vous");
                
                List<Appointment> appointments = appService.getAll();
                System.out.println(" " + appointments.size() + " rendez-vous trouvés");
                
                return ResponseEntity.ok(appointments);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la récupération des rendez-vous: " + e.getMessage()));
        }
    }

    //  AJOUTER UN RENDEZ-VOUS
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody AppointmentDto dto) {
        try {
            System.out.println("➕ Ajout d'un nouveau rendez-vous");
            
            Appointment saved = appService.add(dto);
            System.out.println(" Rendez-vous créé - ID: " + saved.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Données invalides: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de l'ajout du rendez-vous"));
        }
    }

    //  RÉCUPÉRER UN RENDEZ-VOUS PAR ID
    @GetMapping("/get/{Id}")
    public ResponseEntity<?> getAppById(@PathVariable Long Id) {
        try {
            Appointment appointment = appService.getAppById(Id);
            
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Rendez-vous non trouvé"));
            }
            
            //  Vérifier que le docteur a accès à CE rendez-vous
            if (isDoctor()) {
                Long docteurId = getDocteurIdFromToken();
                
                if (appointment.getDoctor() != null && 
                    !appointment.getDoctor().getId().equals(docteurId)) {
                    System.out.println("❌ Docteur " + docteurId + " n'a pas accès au rendez-vous " + Id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Accès refusé à ce rendez-vous"));
                }
            }
            
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la récupération"));
        }
    }

    //  SUPPRIMER UN RENDEZ-VOUS
    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> delete(@PathVariable Long Id) {
        try {
            System.out.println("🗑️ Suppression du rendez-vous ID: " + Id);
            
            //  Vérifier que le docteur peut supprimer CE rendez-vous
            if (isDoctor()) {
                Appointment appointment = appService.getAppById(Id);
                
                if (appointment != null && appointment.getDoctor() != null) {
                    Long docteurId = getDocteurIdFromToken();
                    
                    if (!appointment.getDoctor().getId().equals(docteurId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "Vous ne pouvez pas supprimer ce rendez-vous"));
                    }
                }
            }
            
            appService.delete(Id);
            System.out.println(" Rendez-vous supprimé");
            
            return ResponseEntity.ok(Map.of("message", "Rendez-vous supprimé avec succès"));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la suppression"));
        }
    }


    // Dans votre AppointmentController
    @GetMapping("/patient/{patientId}")
     public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
     Appointment appointments = appService.getAppointmentsByPatient(patientId);
    
    if (appointments == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Collections.emptyList());
    }
    
    return ResponseEntity.ok(appointments);
}

    @GetMapping("/patient/all/{patientId}")
    public ResponseEntity<?> getAllAppointmentsByPatient(@PathVariable Long patientId) {   
    List<Appointment> appointments = appService.getAllAppointmentsByPatient(patientId);
    return ResponseEntity.ok(appointments);
    }

    //  VALIDER UN RENDEZ-VOUS
    @PutMapping("/{id}/validate")
    public ResponseEntity<Map<String, Object>> validateAppointment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            //  Vérifier les permissions
            if (isDoctor()) {
                Long docteurId = getDocteurIdFromToken();
                Appointment appointment = appService.getAppById(id);
                
                if (appointment == null) {
                    response.put("success", false);
                    response.put("message", "Rendez-vous non trouvé");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                
                if (appointment.getDoctor() != null && 
                    !appointment.getDoctor().getId().equals(docteurId)) {
                    response.put("success", false);
                    response.put("message", "Vous ne pouvez pas valider ce rendez-vous");
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //  REJETER UN RENDEZ-VOUS
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectAppointment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            //  Vérifier les permissions
            if (isDoctor()) {
                Long docteurId = getDocteurIdFromToken();
                Appointment appointment = appService.getAppById(id);
                
                if (appointment != null && appointment.getDoctor() != null && 
                    !appointment.getDoctor().getId().equals(docteurId)) {
                    response.put("success", false);
                    response.put("message", "Vous ne pouvez pas rejeter ce rendez-vous");
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //  DÉMARRER UN RENDEZ-VOUS
    @PutMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startAppointment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            //  Vérifier les permissions
            if (isDoctor()) {
                Long docteurId = getDocteurIdFromToken();
                Appointment appointment = appService.getAppById(id);
                
                if (appointment != null && appointment.getDoctor() != null && 
                    !appointment.getDoctor().getId().equals(docteurId)) {
                    response.put("success", false);
                    response.put("message", "Vous ne pouvez pas démarrer ce rendez-vous");
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}