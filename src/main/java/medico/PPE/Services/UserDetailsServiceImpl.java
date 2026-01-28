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
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DoctorateRepository doctorateRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Le nom d'utilisateur ne peut pas être vide");
        }

        String normalizedUsername = username.trim().toLowerCase();
        System.out.println("🔍 Recherche utilisateur: " + normalizedUsername);

        // 1 Chercher parmi les customers
        var customer = customerRepository.findByUsername(normalizedUsername);
        if (customer.isPresent()) {
            System.out.println(" Customer trouvé: " + customer.get().getUsername());
            return new User(
                customer.get().getUsername(),
                customer.get().getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );
        }

        // 2 Chercher parmi les docteurs
        var docteur = doctorateRepository.findByUsername(normalizedUsername);
        if (docteur.isPresent()) {
            System.out.println(" Docteur trouvé: " + docteur.get().getUsername());
            return new User(
                docteur.get().getUsername(),
                docteur.get().getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_DOCTEUR"))
            );
        }

        // 3 Aucun utilisateur trouvé
        System.out.println("❌ Aucun utilisateur trouvé pour: " + normalizedUsername);
        throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'identifiant : " + username);
    }

    /**
     * Vérifie si un username existe déjà (Customer OU Docteur)
     */
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String normalizedUsername = username.trim().toLowerCase();
        return customerRepository.existsByUsername(normalizedUsername) ||
               doctorateRepository.existsByUsername(normalizedUsername);
    }

    /**
     * Vérifie si un email existe déjà (Customer OU Docteur)
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String normalizedEmail = email.trim().toLowerCase();
        return customerRepository.existsByEmail(normalizedEmail) ||
               doctorateRepository.existsByEmail(normalizedEmail);
    }

    /**
     * Détermine le type d'utilisateur
     */
    public String getUserType(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String normalizedUsername = username.trim().toLowerCase();
        if (customerRepository.findByUsername(normalizedUsername).isPresent()) {
            return "CUSTOMER";
        } else if (doctorateRepository.findByUsername(normalizedUsername).isPresent()) {
            return "DOCTEUR";
        }
        return null;
    }
 /**
     * Récupère l'ID utilisateur par username
     */
    public Long getUserIdByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String normalizedUsername = username.trim().toLowerCase();
        System.out.println("🔍 Recherche ID pour username: " + normalizedUsername);

        // Chercher d'abord dans Customer
        Optional<Customer> customer = customerRepository.findByUsername(normalizedUsername);
        if (customer.isPresent()) {
            Long id = customer.get().getId();
            System.out.println("✅ ID Customer trouvé: " + id);
            return id;
        }

        // Chercher ensuite dans Docteur
        Optional<Docteur> docteur = doctorateRepository.findByUsername(normalizedUsername);
        if (docteur.isPresent()) {
            Long id = docteur.get().getId();
            System.out.println("✅ ID Docteur trouvé: " + id);
            return id;
        }

        System.out.println("❌ Aucun ID trouvé pour username: " + normalizedUsername);
        return null;
    }

}