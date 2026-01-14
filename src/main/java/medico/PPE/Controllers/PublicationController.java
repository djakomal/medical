package medico.PPE.Controllers;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import medico.PPE.Models.Publication;
import medico.PPE.Services.ConseilService;
import medico.PPE.Services.PublicationService;
import medico.PPE.dtos.ConseilDto;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/publication")
public class PublicationController {
    
    @Autowired
    private PublicationService publicationService;
    
    @PostMapping
    public ResponseEntity<Publication> creerPublication(@RequestBody Publication publication) {
        try {
            Publication createdPublication = publicationService.creerPublication(publication);
            return new ResponseEntity<>(createdPublication, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer tous les conseils
    @GetMapping
    public ResponseEntity<List<Publication>> getAllPublication() {
        try {
            List<Publication> publications = publicationService.getAllPublication();
            return new ResponseEntity<>(publications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer les conseils publiés
    @GetMapping("/publies")
    public ResponseEntity<List<Publication>> getPublicationPublies() {
        try {
            List<Publication> publications = publicationService.getPublicationPublies();
            return new ResponseEntity<>(publications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer un conseil par ID
    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable Long id) {
        try {
            Publication publication = publicationService.getPublicationById(id);
            return new ResponseEntity<>(publication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    // Publier/Dépublier un conseil
    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<Publication> togglePublish(@PathVariable Long id) {
        try {
            Publication updatedPublication = publicationService.togglePublish(id);
            return new ResponseEntity<>(updatedPublication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Supprimer un conseil
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
