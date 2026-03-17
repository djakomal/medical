package medico.PPE.Services;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Models.OtpValidation;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.dtos.DocteurResponse;
import medico.PPE.dtos.SignupRequest;
import medico.PPE.utils.JwtUtil;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final DoctorateRepository doctorateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private OtpValidationService validationService;

    @Autowired
    public AuthServiceImpl(CustomerRepository customerRepository,
                          DoctorateRepository doctorateRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          UserDetailsServiceImpl userDetailsService,
                          OtpValidationService validationService) {
        this.customerRepository = customerRepository;
        this.doctorateRepository = doctorateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.validationService = validationService;
    }
    @Override
    public Customer createCustomer(SignupRequest signupRequest) {
    
        String normalizedUsername = signupRequest.getUsername().trim().toLowerCase();
        String normalizedEmail    = signupRequest.getEmail().trim().toLowerCase();
    
        Optional<Customer> existingCustomer = customerRepository.findByEmail(normalizedEmail);
        if (existingCustomer.isPresent()) {
            Customer existing = existingCustomer.get();
    
            if (!existing.isEnabled()) {
                // Renvoyer un nouveau code OTP
                SignupRequest dto = new SignupRequest();
                dto.setId(existing.getId());
                validationService.save(dto);
                throw new RuntimeException("Un code de validation a été renvoyé à votre email.");
            }
    
            // Compte déjà activé
            throw new RuntimeException("Cet email est déjà utilisé");
        }
    
        // ✅ Même vérification pour le username
        if (userDetailsService.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }
    
        // Création normale
        Customer customer = new Customer();
        customer.setUsername(normalizedUsername);
        customer.setEmail(normalizedEmail);
        customer.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        customer.setEnabled(false);
    
        Customer saved = customerRepository.save(customer);
    
        SignupRequest dto = new SignupRequest();
        dto.setId(saved.getId());
        validationService.save(dto);
    
        return saved;
    }

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    // ========== DOCTEUR ==========

@Override
    public DocteurResponse createDocteur(Docteur docteur) {

    if (docteur.getUsername() == null || docteur.getUsername().isBlank())
        throw new RuntimeException("Le nom d'utilisateur est obligatoire");
    if (docteur.getEmail() == null || docteur.getEmail().isBlank())
        throw new RuntimeException("L'email est obligatoire");
    if (docteur.getPassword() == null || docteur.getPassword().isBlank())
        throw new RuntimeException("Le mot de passe est obligatoire");

    String normalizedUsername = docteur.getUsername().trim().toLowerCase();
    String normalizedEmail    = docteur.getEmail().trim().toLowerCase();

    // ✅ Vérifier si l'email existe déjà
    Optional<Docteur> existingByEmail = doctorateRepository.findByEmail(normalizedEmail);
    if (existingByEmail.isPresent()) {
        Docteur existing = existingByEmail.get();
        if (!existing.isEnabled()) {
            // Compte non activé → renvoyer un OTP et retourner la réponse normalement
            validationService.saveForDocteur(existing.getId());
            return new DocteurResponse(existing, null); // ✅ pas d'exception
        }
        throw new RuntimeException("Cet email est déjà utilisé");
    }

    // ✅ Vérifier si le username existe déjà
    Optional<Docteur> existingByUsername = doctorateRepository.findByUsername(normalizedUsername);
    if (existingByUsername.isPresent()) {
        Docteur existing = existingByUsername.get();
        if (!existing.isEnabled()) {
            // Compte non activé → renvoyer un OTP et retourner la réponse normalement
            validationService.saveForDocteur(existing.getId());
            return new DocteurResponse(existing, null); // ✅ pas d'exception
        }
        throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
    }

    // Création normale
    docteur.setUsername(normalizedUsername);
    docteur.setEmail(normalizedEmail);
    docteur.setPassword(passwordEncoder.encode(docteur.getPassword()));
    docteur.setEnabled(false);

    Docteur createdDocteur = doctorateRepository.save(docteur);
    validationService.saveForDocteur(createdDocteur.getId());

    return new DocteurResponse(createdDocteur, null);
}
    @Override
    public List<Docteur> getAllDocteur() {
        return doctorateRepository.findAll();
    }

    @Override
    public void deleteDocteur(Long id) {
        doctorateRepository.deleteById(id);
    }



    @Override
public void activation(Map<String, String> activation) {
    OtpValidation validation = validationService.readCode(activation.get("code"));

    if (Instant.now().isAfter(validation.getExpireOtp()))
        throw new RuntimeException("Votre code a expiré");

    // ✅ Cas Docteur
    if (validation.getDocteur() != null) {
        Docteur docteur = doctorateRepository
            .findById(validation.getDocteur().getId())
            .orElseThrow(() -> new RuntimeException("Docteur introuvable"));
        docteur.setEnabled(true);
        doctorateRepository.save(docteur);
        return;
    }

    // ✅ Cas Customer
    if (validation.getCustomer() != null) {
        Customer customer = customerRepository
            .findById(validation.getCustomer().getId())
            .orElseThrow(() -> new RuntimeException("Customer introuvable"));
        customer.setEnabled(true);
        customerRepository.save(customer);
        return;
    }

    throw new RuntimeException("Aucun utilisateur associé à ce code");
}
}

