package medico.PPE.Controllers;


import medico.PPE.Models.Customer;

import medico.PPE.Models.Docteur;
import medico.PPE.Services.AuthService;
import medico.PPE.Services.DoctorateServiceImpl;
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


    @Autowired
    public SignupController(AuthService authService) {
        this.authService = authService;

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
    public ResponseEntity<?> addDocteur(@RequestBody Docteur docteur) {
        try {
            // Appeler le service pour créer un Docteur et obtenir le DocteurResponse
            DocteurResponse createdDocteurResponse = authService.createDocteur(docteur);

            // Vérifier si la création a réussi
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocteurResponse);
        } catch (RuntimeException e) {
            // Gérer les erreurs si la création échoue (ex : email déjà existant)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create docteur: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Docteur> getAllDocteur(){
        return authService.getAllDocteur();
    }

    @DeleteMapping("/docteur/{id}")
    public void deleteDocteur(@PathVariable Long id){
        authService.deleteDocteur(id);
    }
}
