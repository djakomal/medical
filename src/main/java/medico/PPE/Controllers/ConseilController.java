package medico.PPE.Controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import medico.PPE.Models.Conseil;
import medico.PPE.Services.ConseilService;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/conseils")
public class ConseilController {
    
    @Autowired
    private ConseilService conseilService;
    
    // Créer un nouveau conseil
    @PostMapping
    public ResponseEntity<Conseil> creerConseil(@RequestBody Conseil conseil, Principal principal) {
        try {
            
            if (principal == null) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            
            String username = principal.getName();
            Conseil createdConseil = conseilService.creerConseil(conseil, username);
            
            return new ResponseEntity<>(createdConseil, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer tous les conseils
    @GetMapping
    public ResponseEntity<List<Conseil>> getAllConseils(Principal principal) {
        try {
            if (principal == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
    
            List<Conseil> conseils = conseilService.getAllConseils(principal.getName());
            return new ResponseEntity<>(conseils, HttpStatus.OK);
    
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer les conseils publiés
    @GetMapping("/publies")
    public ResponseEntity<List<Conseil>> getConseilsPublies() {
        try {
            List<Conseil> conseils = conseilService.getConseilsPublies();
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un conseil par ID
    @GetMapping("/{id}")
    public ResponseEntity<Conseil> getConseilById(@PathVariable Long id) {
        try {
            Conseil conseil = conseilService.getConseilById(id);
            return new ResponseEntity<>(conseil, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Récupérer les conseils par catégorie
    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<Conseil>> getConseilsByCategorie(@PathVariable String categorie) {
        try {
            List<Conseil> conseils = conseilService.getConseilsByCategorie(categorie);
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer les conseils par auteur
    @GetMapping("/auteur/{auteur}")
    public ResponseEntity<List<Conseil>> getConseilsByAuteur(@PathVariable String auteur) {
        try {
            List<Conseil> conseils = conseilService.getConseilsByAuteur(auteur);
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Rechercher des conseils
    @GetMapping("/recherche")
    public ResponseEntity<List<Conseil>> rechercherConseils(@RequestParam String query) {
        try {
            List<Conseil> conseils = conseilService.rechercherConseils(query);
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Mettre à jour un conseil
    @PutMapping("/{id}")
    public ResponseEntity<Conseil> updateConseil(
            @PathVariable Long id, 
            @RequestBody Conseil conseil) {
        try {
            Conseil updatedConseil = conseilService.updateConseil(id, conseil);
            return new ResponseEntity<>(updatedConseil, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Publier/Dépublier un conseil
    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<Conseil> togglePublish(@PathVariable Long id) {
        try {
            Conseil updatedConseil = conseilService.togglePublish(id);
            return new ResponseEntity<>(updatedConseil, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Supprimer un conseil
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteConseil(@PathVariable Long id) {
        try {
            conseilService.deleteConseil(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Récupérer les conseils par docteur
    @GetMapping("/docteur/{docteurId}")
    public ResponseEntity<List<Conseil>> getConseilsByDocteur(@PathVariable Long docteurId) {
        try {
            List<Conseil> conseils = conseilService.getConseilsByDocteur(docteurId);
            return ResponseEntity.ok(conseils);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}