package medico.PPE.Controllers;


import medico.PPE.Models.Customer;

import medico.PPE.Models.User;
import medico.PPE.Services.AuthService;
import medico.PPE.dtos.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/delete/{id}")
    public  void delete(@PathVariable Long id){
        authService.delete(id);
    }

    @GetMapping("")
    public List<Customer> getAll() {
        return authService.getAll();

    }
    @GetMapping("/get/{id}")
    public Customer getCustomerById(@PathVariable Long id){
        return  authService.getCustomerById(id);
    }

}
