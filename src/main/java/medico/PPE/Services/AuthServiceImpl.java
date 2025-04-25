package medico.PPE.Services;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.dtos.DocteurDto;
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
    @Autowired
    public AuthServiceImpl(CustomerRepository customerRepository, DoctorateRepository doctorateRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.doctorateRepository = doctorateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    @Override
    public Customer createCustomer(SignupRequest signupRequest) {
        //Check if customer already exist
        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            return null;
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(signupRequest,customer);

        //Hash the password before saving
        String hashPassword = passwordEncoder.encode(signupRequest.getPassword());
        customer.setPassword(hashPassword);
        Customer createdCustomer = customerRepository.save(customer);
        customer.setId(createdCustomer.getId());
        return customer;
    }
    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
    @Override
    public void delete(Long Id) {
        /*Optional<Customer> optionalUser = userRepository.findById(Id);*/
        customerRepository.deleteById(Id);

    }
    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }


    //Docteur
    //
    //

    @Override
    public DocteurResponse createDocteur(DocteurDto docteurDto) {
        // Vérifier si le docteur existe déjà
        if (doctorateRepository.existsByEmail(docteurDto.getEmail())) {
            throw new RuntimeException("email existe deja");
        }

        Docteur docteur = new Docteur();
        BeanUtils.copyProperties(docteurDto, docteur);

        // Hasher le mot de passe avant de l'enregistrer
        String hashPassword = passwordEncoder.encode(docteurDto.getPassword());
        docteur.setPassword(hashPassword);

        // Sauvegarder le docteur dans la base de données
        Docteur createdDocteur = doctorateRepository.save(docteur);
        docteur.setId(createdDocteur.getId());

        // Créer un token JWT après la création du docteur
        String token = jwtUtil.generateToken(docteur.getEmail());

        // Retourner un objet contenant le Docteur et le token
        return new DocteurResponse(docteur, token);
    }


    @Override
    public List<Docteur> getAllDocteur() {
        return doctorateRepository.findAll();
    }
    @Override
    public void deleteDocteur(Long Id) {
        /*Optional<Customer> optionalUser = userRepository.findById(Id);*/
        doctorateRepository.deleteById(Id);

    }


}
