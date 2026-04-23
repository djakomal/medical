package medico.PPE.Controllers;

import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Services.CreneauServiceImpl;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/creneaux")
@CrossOrigin(origins = "http://localhost:5173")
public class CreneauController {
    
    @Autowired
    private CreneauServiceImpl creneauService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private DoctorateRepository doctorateRepository;
    
    //  RÉCUPÉRER L'ID DU DOCTEUR CONNECTÉ VIA SecurityContextHolder
    private Long getDocteurIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Docteur docteur = doctorateRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        
        return docteur.getId();
    }
    
    //  VÉRIFIER SI L'UTILISATEUR EST UN DOCTEUR
    private boolean isDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(a -> "ROLE_DOCTOR".equals(a.getAuthority()));
    }
    
    //  RÉCUPÉRER TOUS LES CRÉNEAUX (filtré par docteur si c'est un docteur)
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            System.out.println("📅 GET /api/creneaux - Récupération des créneaux");
            
            if (isDoctor()) {
                Long doctorId = getDocteurIdFromToken();
                System.out.println("👨‍⚕️ Docteur ID: " + doctorId + " - Ses créneaux uniquement");
                
                List<Creneau> creneaux = creneauService.getCreneauxByDocteur(doctorId);
                System.out.println("✅ " + creneaux.size() + " créneaux trouvés");
                
                return ResponseEntity.ok(creneaux);
            } else {
                System.out.println("👤 User - Créneaux disponibles");
                
                // Pour les patients, afficher les créneaux disponibles (non-private ou vérifiés)
                List<Creneau> creneaux = creneauService.getCreneauxDisponibles(null);
                System.out.println("✅ " + creneaux.size() + " créneaux disponibles");
                
                return ResponseEntity.ok(creneaux);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la récupération des créneaux: " + e.getMessage()));
        }
    }
    
    //  RÉCUPÉRER MES CRÉNEAUX (du docteur connecté)
  
    @GetMapping("/mes-creneaux")
    public ResponseEntity<?> getMesCreneaux() {
        try {
            System.out.println("📅 GET /api/creneaux/mes-creneaux");
            
            Long doctorId = getDocteurIdFromToken();
            System.out.println("👨‍⚕️ Docteur ID: " + doctorId);
            
            List<Creneau> creneaux = creneauService.getCreneauxByDocteur(doctorId);
            System.out.println("✅ " + creneaux.size() + " créneaux trouvés");
            
            return ResponseEntity.ok(creneaux);
        } catch (RuntimeException e) {
            System.err.println("❌ Authentification requise: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Authentification requise"));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la récupération de vos créneaux"));
        }
    }
    
    //  AJOUTER UN CRÉNEAU
    @PostMapping
    public ResponseEntity<?> ajouterCreneau(@RequestBody Creneau creneauRequest) {
        try {
            System.out.println("➕ POST /api/creneaux - Ajout d'un créneau");
            
            Long doctorId = getDocteurIdFromToken();
            System.out.println("👨‍⚕️ Docteur ID: " + doctorId);
            
            Creneau creneau = creneauService.ajouterCreneau(doctorId, creneauRequest);
            System.out.println("✅ Créneau créé - ID: " + creneau.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(creneau);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Données invalides: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur authentification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Authentification requise"));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de l'ajout du créneau"));
        }
    }
    
    //  MODIFIER UN CRÉNEAU

    @PutMapping("/{id}")
    public ResponseEntity<?> modifierCreneau(
            @PathVariable Long id,
            @RequestBody Creneau creneauRequest) {
        try {
            System.out.println("✏️ PUT /api/creneaux/" + id + " - Modification d'un créneau");
            
            Long doctorId = getDocteurIdFromToken();
            System.out.println("👨‍⚕️ Docteur ID: " + doctorId);
            
            Creneau creneau = creneauService.modifierCreneau(doctorId, id, creneauRequest);
            System.out.println("✅ Créneau modifié");
            
            return ResponseEntity.ok(creneau);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Données invalides: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("❌ Accès refusé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Vous n'avez pas accès à ce créneau"));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la modification"));
        }
    }
    
    //  SUPPRIMER UN CRÉNEAU
  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerCreneau(@PathVariable Long id) {
        try {
            System.out.println("🗑️ DELETE /api/creneaux/" + id);
            
            Long doctorId = getDocteurIdFromToken();
            System.out.println("👨‍⚕️ Docteur ID: " + doctorId);
            
            creneauService.supprimerCreneau(doctorId, id);
            System.out.println("✅ Créneau supprimé");
            
            return ResponseEntity.ok(Map.of("message", "Créneau supprimé avec succès"));
        } catch (RuntimeException e) {
            System.err.println("❌ Accès refusé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Vous n'avez pas accès à ce créneau"));
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la suppression"));
        }
    }
    


    @GetMapping("/docteur/{doctorId}")
    public ResponseEntity<?> getCreneauxDocteur(@PathVariable("doctorId") Long doctorId) {
        try {
            System.out.println("📅 GET /api/creneaux/docteur/" + doctorId);
            
            List<Creneau> creneaux = creneauService.getCreneauxDisponibles(doctorId);
            System.out.println("✅ " + creneaux.size() + " créneaux disponibles trouvés");
            
            return ResponseEntity.ok(creneaux);
        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de la récupération des créneaux"));
        }
    }

    @GetMapping("/{id}/disponibilite")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Map<String, Boolean>> verifierDisponibilite(@PathVariable Long id) {
        try {
            boolean disponible = creneauService.estDisponible(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("disponible", disponible);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // NOUVEL ENDPOINT : Marquer comme indisponible
    @PutMapping("/{id}/indisponible")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Creneau> marquerIndisponible(@PathVariable Long id) {
        try {
            Creneau creneau = creneauService.marquerIndisponible(id);
            return ResponseEntity.ok(creneau);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}