package medico.PPE.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import medico.PPE.Models.Publication;
import medico.PPE.Services.PublicationService;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/publication")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;  // ← instance, pas classe

    // ── Créer ──────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Publication> creerPublication(
            @RequestBody Publication publication,
            Principal principal) {
        try {
            if (principal == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            // ← passer principal.getName() pour associer le docteur
            Publication created = publicationService.creerPublication(publication, principal.getName());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ── Lire tous ──────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Publication>> getAllPublications(Principal principal) {
        try {
            if (principal == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(
                publicationService.getAllPublication(principal.getName()), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ── Publiés seulement ──────────────────────────────────
    @GetMapping("/publies")
    public ResponseEntity<List<Publication>> getPublicationPublies() {
        try {
            return new ResponseEntity<>(publicationService.getPublicationPublies(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ── Lire par ID ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(publicationService.getPublicationById(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ── Mettre à jour ──────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Publication> updatePublication(
            @PathVariable Long id,
            @RequestBody Publication publication) {
        try {
            // ← publicationService (instance), pas PublicationService (classe)
            Publication updated = publicationService.updatePublication(id, publication);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ── Toggle publish ─────────────────────────────────────
    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<Publication> togglePublish(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(publicationService.togglePublish(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ── Supprimer ──────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePublication(@PathVariable Long id) {
        try {
            publicationService.deletePublication(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}