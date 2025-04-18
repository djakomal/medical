package medico.PPE.Controllers;

import medico.PPE.Models.Docteur;
import medico.PPE.Services.DocteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/docteur")
@CrossOrigin(origins = "http://localhost:4200")
public class DocteurController {
    @Autowired
    private DocteurService docteurService;

    // Inscription d'un docteur avec ses creanaux
    @PostMapping("/add")
    public Docteur Add(@RequestBody Docteur docteur) {
        try {
            Docteur nouveau = docteurService.add(docteur);
            return docteurService.add(nouveau);
        } catch (IllegalArgumentException e) {
            return docteurService.badRequest().body(e.getMessage());
        }

    }
    @GetMapping
    public List<Docteur> getAllDocteur(){
        return docteurService.getAllDocteur();
    }
    @GetMapping("/{id}")
    public Optional<Docteur> getDocteurById(@PathVariable Long id){
        return docteurService.getById(id);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
         docteurService.deleteDocteur(id);
    }
}
