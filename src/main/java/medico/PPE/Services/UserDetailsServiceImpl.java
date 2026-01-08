package medico.PPE.Services;

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

        // 1️⃣ Chercher parmi les customers
        return customerRepository.findByUsername(normalizedUsername)
            .map(customer -> {
                System.out.println("✅ Customer trouvé: " + customer.getUsername() + " (email: " + customer.getEmail() + ")");
                return new User(
                    customer.getUsername(),
                    customer.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                );
            })
            // 2️⃣ Sinon chercher parmi les docteurs
            .orElseGet(() -> doctorateRepository.findByUsername(normalizedUsername)
                .map(docteur -> {
                    System.out.println("✅ Docteur trouvé: " + docteur.getUsername() + " (email: " + docteur.getEmail() + ")");
                    return new User(
                        docteur.getUsername(),
                        docteur.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTEUR"))
                    );
                })
                .orElseThrow(() -> {
                    System.out.println("❌ Aucun utilisateur trouvé pour: " + normalizedUsername);
                    return new UsernameNotFoundException(
                        "Aucun utilisateur trouvé avec l'identifiant : " + username
                    );
                })
            );
    }

    /**
     * Vérifie si un username existe déjà
     */
    public boolean existsByUsername(String username) {
        String normalizedUsername = username.trim().toLowerCase();
        return customerRepository.findByUsername(normalizedUsername).isPresent() ||
               doctorateRepository.findByUsername(normalizedUsername).isPresent();
    }

    /**
     * Détermine le type d'utilisateur
     */
    public String getUserType(String username) {
        String normalizedUsername = username.trim().toLowerCase();
        if (customerRepository.findByUsername(normalizedUsername).isPresent()) {
            return "CUSTOMER";
        } else if (doctorateRepository.findByUsername(normalizedUsername).isPresent()) {
            return "DOCTEUR";
        }
        return null;
    }
}