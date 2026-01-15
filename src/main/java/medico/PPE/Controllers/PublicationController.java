package medico.PPE.Controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import medico.PPE.Models.Publication;
import medico.PPE.Services.PublicationService;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/publication")
public class PublicationController {
    
    @Autowired
    private PublicationService publicationService;
    
    // Créer un nouveau Publication
    @PostMapping
    public ResponseEntity<Publication> creerPublication(@RequestBody Publication publication, Principal principal) {
        try {
            
            if (principal == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            
            // String username = principal.getName();
            Publication createdPublication = publicationService.creerPublication(publication);
            
            return new ResponseEntity<>(createdPublication, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer tous les Publications
    @GetMapping
    public ResponseEntity<List<Publication>> getAllPublications() {
        try {
            List<Publication> publications = publicationService.getAllPublication();
            return new ResponseEntity<>(publications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer les Publications publiés
    @GetMapping("/publies")
    public ResponseEntity<List<Publication>> getPublicationPublies() {
        try {
            List<Publication> publication = publicationService.getPublicationPublies();
            return new ResponseEntity<>(publication, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un Publication par ID
    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable Long id) {
        try {
            Publication publication = publicationService.getPublicationById(id);
            return new ResponseEntity<>(publication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    
    // Mettre à jour un Publication
    // @PutMapping("/{id}")
    // public ResponseEntity<Publication> updatePublication(
    //         @PathVariable Long id, 
    //         @RequestBody Publication publication) {
    //     try {
    //         Publication updatedPublication = PublicationService.updatePublication(id, publication);
    //         return new ResponseEntity<>(updatedPublication, HttpStatus.OK);
    //     } catch (RuntimeException e) {
    //         return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    //     }
    // }
    
    // Publier/Dépublier un Publication
    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<Publication> togglePublish(@PathVariable Long id) {
        try {
            Publication updatedPublication = publicationService.togglePublish(id);
            return new ResponseEntity<>(updatedPublication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Supprimer un Publication
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