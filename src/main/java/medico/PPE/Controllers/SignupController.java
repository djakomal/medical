package medico.PPE.Controllers;

import lombok.extern.slf4j.Slf4j;          
import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Services.AuthService;
import medico.PPE.dtos.DocteurResponse;
import medico.PPE.dtos.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j                                 
@RestController
@RequestMapping("/signup")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173"})
public class SignupController {

    private final AuthService authService;

    @Autowired
    public SignupController(AuthService authService) {
        this.authService = authService;
    }

    // ── Customer ──────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> signupCustomer(@RequestBody SignupRequest signupRequest) {
        try {
            Customer createdCustomer = authService.createCustomer(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create customer: " + e.getMessage());
        }
    }
    @PostMapping("/code-activation")
    public ResponseEntity<?> activation(@RequestBody Map<String, String> activation) {
        try {
            this.authService.activation(activation);
            log.info("Activation réussie");    // ✅ log maintenant disponible via @Slf4j
            return ResponseEntity.ok("Compte activé avec succès");
        } catch (RuntimeException e) {
            log.error("Erreur activation : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(authService.getAll());
    }

    @GetMapping("/get/user/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = authService.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer introuvable");
        }
        return ResponseEntity.ok(customer);
    }

    // ── Docteur ───────────────────────────────────────────────────────────

    @PostMapping("/docteur/add")
    public ResponseEntity<?> addDocteur(@RequestBody Docteur docteur) {
        try {
            DocteurResponse createdDocteurResponse = authService.createDocteur(docteur);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocteurResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create docteur: " + e.getMessage());
        }
    }

    @GetMapping("/docteur/all")
    public ResponseEntity<List<Docteur>> getAllDocteur() {
        return ResponseEntity.ok(authService.getAllDocteur());
    }

    @DeleteMapping("/docteur/{id}")
    public ResponseEntity<Void> deleteDocteur(@PathVariable Long id) {
        authService.deleteDocteur(id);
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/docteur/change-password")
    public ResponseEntity<?> changePassword(
        @RequestBody Map<String, String> payload,
        @RequestHeader("Authorization") String authHeader) {
    try {
        String token = authHeader.replace("Bearer ", "");
        authService.changePasswordDocteur(token, payload);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
}