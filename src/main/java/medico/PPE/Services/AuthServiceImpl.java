package medico.PPE.Services;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.dtos.DocteurResponse;
import medico.PPE.dtos.SignupRequest;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final DoctorateRepository doctorateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthServiceImpl(CustomerRepository customerRepository,
                          DoctorateRepository doctorateRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          UserDetailsServiceImpl userDetailsService) {
        this.customerRepository = customerRepository;
        this.doctorateRepository = doctorateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Customer createCustomer(SignupRequest signupRequest) {
        // ✅ Validation des champs
        if (signupRequest.getUsername() == null || signupRequest.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Le nom d'utilisateur est obligatoire");
        }
        if (signupRequest.getEmail() == null || signupRequest.getEmail().trim().isEmpty()) {
            throw new RuntimeException("L'email est obligatoire");
        }
        if (signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty()) {
            throw new RuntimeException("Le mot de passe est obligatoire");
        }

        String normalizedUsername = signupRequest.getUsername().trim().toLowerCase();
        String normalizedEmail = signupRequest.getEmail().trim().toLowerCase();

        // ✅ Vérifier si le username existe déjà (Customer OU Docteur)
        if (userDetailsService.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        // ✅ Vérifier si l'email existe déjà (Customer OU Docteur)
        if (userDetailsService.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(signupRequest, customer);

        // Normaliser les données
        customer.setUsername(normalizedUsername);
        customer.setEmail(normalizedEmail);

        // Hash le mot de passe
        String hashPassword = passwordEncoder.encode(signupRequest.getPassword());
        customer.setPassword(hashPassword);

        // Sauvegarder
        Customer createdCustomer = customerRepository.save(customer);
        System.out.println("✅ Customer créé: " + createdCustomer.getUsername());
        
        return createdCustomer;
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
        // ✅ Validation des champs
        if (docteur.getUsername() == null || docteur.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Le nom d'utilisateur est obligatoire");
        }
        if (docteur.getEmail() == null || docteur.getEmail().trim().isEmpty()) {
            throw new RuntimeException("L'email est obligatoire");
        }
        if (docteur.getPassword() == null || docteur.getPassword().isEmpty()) {
            throw new RuntimeException("Le mot de passe est obligatoire");
        }

        String normalizedUsername = docteur.getUsername().trim().toLowerCase();
        String normalizedEmail = docteur.getEmail().trim().toLowerCase();

        // ✅ Vérifier si le username existe déjà (Customer OU Docteur)
        if (userDetailsService.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        // ✅ Vérifier si l'email existe déjà (Customer OU Docteur)
        if (userDetailsService.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Normaliser les données
        docteur.setUsername(normalizedUsername);
        docteur.setEmail(normalizedEmail);

        // Hasher le mot de passe
        String hashPassword = passwordEncoder.encode(docteur.getPassword());
        docteur.setPassword(hashPassword);

        // Sauvegarder
        Docteur createdDocteur = doctorateRepository.save(docteur);
        System.out.println("✅ Docteur créé: " + createdDocteur.getUsername());

        // Créer un token JWT
        String token = jwtUtil.generateToken(createdDocteur.getUsername());

        return new DocteurResponse(createdDocteur, token);
    }

    @Override
    public List<Docteur> getAllDocteur() {
        return doctorateRepository.findAll();
    }

    @Override
    public void deleteDocteur(Long id) {
        doctorateRepository.deleteById(id);
    }
}