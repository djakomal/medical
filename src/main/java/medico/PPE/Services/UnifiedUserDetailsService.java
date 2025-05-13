package medico.PPE.Services;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnifiedUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorateRepository docteurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Essayer d'abord avec customer
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return new User(customer.getEmail(), customer.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        }

        // Sinon essayer avec docteur
        Optional<Docteur> docteurOpt = docteurRepository.findByEmail(email);
        if (docteurOpt.isPresent()) {
            Docteur docteur = docteurOpt.get();
            return new User(docteur.getEmail(), docteur.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_DOCTEUR")));
        }

        throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);
    }
}
