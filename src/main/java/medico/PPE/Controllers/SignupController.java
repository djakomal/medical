package medico.PPE.Controllers;


import medico.PPE.Models.Customer;

import medico.PPE.Models.Docteur;
import medico.PPE.Services.AuthService;
import medico.PPE.Services.DocteurService;
import medico.PPE.dtos.DocteurDto;
import medico.PPE.dtos.DocteurResponse;
import medico.PPE.dtos.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/signup")
@CrossOrigin(origins = "http://localhost:5173")
public class SignupController {

    private final AuthService authService;
    private final DocteurService docteurService;

    @Autowired
    public SignupController(AuthService authService, DocteurService docteurService) {
        this.authService = authService;
        this.docteurService = docteurService;
    }

    @PostMapping
    public ResponseEntity<?> signupCustomer(@RequestBody SignupRequest signupRequest) {
        Customer createdCustomer = authService.createCustomer(signupRequest);
        if (createdCustomer != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create customer");
        }
    }

    @DeleteMapping("/delete/user/{id}")
    public  void delete(@PathVariable Long id){
        authService.delete(id);
    }

    @GetMapping("")
    public List<Customer> getAll() {
        return authService.getAll();

    }
    @GetMapping("/get/user/{id}")
    public Customer getCustomerById(@PathVariable Long id){
        return  authService.getCustomerById(id);
    }
 // Docteur .................................................................



    @PostMapping("/docteur/add")
    public ResponseEntity<?> addDocteur(@RequestBody DocteurDto docteurDto) {
        try {
            // Appeler le service pour créer un Docteur et obtenir le DocteurResponse
            DocteurResponse createdDocteurResponse = authService.createDocteur(docteurDto);

            // Vérifier si la création a réussi
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocteurResponse);
        } catch (RuntimeException e) {
            // Gérer les erreurs si la création échoue (ex : email déjà existant)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create docteur: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Docteur> getAllDocteur(){
        return docteurService.getAllDocteur();
    }
    @GetMapping("/get/docteur/{id}")
    public Optional<Docteur> getDocteurById(@PathVariable Long id){
        return docteurService.getById(id);
    }
    @DeleteMapping("/docteur/{id}")
    public void deleteDocteur(@PathVariable Long id){
        docteurService.deleteDocteur(id);
    }
}
