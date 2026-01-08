package medico.PPE.Controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import medico.PPE.Services.ConseilService;
import medico.PPE.dtos.ConseilDto;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/conseils")
public class ConseilController {
    
    @Autowired
    private ConseilService conseilService;
    
    // Créer un nouveau conseil
    @PostMapping
    public ResponseEntity<ConseilDto> creerConseil(@RequestBody ConseilDto conseilDTO) {
        try {
            ConseilDto createdConseil = conseilService.creerConseil(conseilDTO);
            return new ResponseEntity<>(createdConseil, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer tous les conseils
    @GetMapping
    public ResponseEntity<List<ConseilDto>> getAllConseils() {
        try {
            List<ConseilDto> conseils = conseilService.getAllConseils();
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer les conseils publiés
    @GetMapping("/publies")
    public ResponseEntity<List<ConseilDto>> getConseilsPublies() {
        try {
            List<ConseilDto> conseils = conseilService.getConseilsPublies();
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer un conseil par ID
    @GetMapping("/{id}")
    public ResponseEntity<ConseilDto> getConseilById(@PathVariable Long id) {
        try {
            ConseilDto conseil = conseilService.getConseilById(id);
            return new ResponseEntity<>(conseil, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Récupérer les conseils par catégorie
    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<ConseilDto>> getConseilsByCategorie(@PathVariable String categorie) {
        try {
            List<ConseilDto> conseils = conseilService.getConseilsByCategorie(categorie);
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Récupérer les conseils par auteur
    @GetMapping("/auteur/{auteur}")
    public ResponseEntity<List<ConseilDto>> getConseilsByAuteur(@PathVariable String auteur) {
        try {
            List<ConseilDto> conseils = conseilService.getConseilsByAuteur(auteur);
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Rechercher des conseils
    @GetMapping("/recherche")
    public ResponseEntity<List<ConseilDto>> rechercherConseils(@RequestParam String query) {
        try {
            List<ConseilDto> conseils = conseilService.rechercherConseils(query);
            return new ResponseEntity<>(conseils, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Mettre à jour un conseil
    @PutMapping("/{id}")
    public ResponseEntity<ConseilDto> updateConseil(
            @PathVariable Long id, 
            @RequestBody ConseilDto conseilDTO) {
        try {
            ConseilDto updatedConseil = conseilService.updateConseil(id, conseilDTO);
            return new ResponseEntity<>(updatedConseil, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    // Publier/Dépublier un conseil
    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<ConseilDto> togglePublish(@PathVariable Long id) {
        try {
            ConseilDto updatedConseil = conseilService.togglePublish(id);
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
}