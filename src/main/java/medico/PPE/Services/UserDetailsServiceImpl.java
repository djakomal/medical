package medico.PPE.Services;

import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private DoctorateRepository doctorateRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Le nom d'utilisateur ne peut pas être vide");
        }

        String n = username.trim().toLowerCase();

        // ✅ 1 seule requête — Customer avec ID inclus
        Optional<Customer> customer = customerRepository.findByUsername(n);
        if (customer.isPresent()) {
            Customer c = customer.get();
            return new CustomUserDetails(
                c.getUsername(),
                c.getPassword(),
                c.isEnabled(),
                c.getId(),
                List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
            );
        }

        // ✅ 1 seule requête — Docteur avec ID inclus
        Optional<Docteur> docteur = doctorateRepository.findByUsername(n);
        if (docteur.isPresent()) {
            Docteur d = docteur.get();
            return new CustomUserDetails(
                d.getUsername(),
                d.getPassword(),
                d.isEnabled(),
                d.getId(),
                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
            );
        }

        throw new UsernameNotFoundException("Aucun utilisateur trouvé : " + username);
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        String n = username.trim().toLowerCase();
        return customerRepository.existsByUsername(n) ||
               doctorateRepository.existsByUsername(n);
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String n = email.trim().toLowerCase();
        return customerRepository.existsByEmail(n) ||
               doctorateRepository.existsByEmail(n);
    }

    public String getUserType(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        String n = username.trim().toLowerCase();
        if (customerRepository.findByUsername(n).isPresent()) return "CUSTOMER";
        if (doctorateRepository.findByUsername(n).isPresent()) return "DOCTEUR";
        return null;
    }

    // Gardé pour compatibilité avec d'autres parties du code
    public Long getUserIdByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        String n = username.trim().toLowerCase();
        Optional<Customer> c = customerRepository.findByUsername(n);
        if (c.isPresent()) return c.get().getId();
        Optional<Docteur> d = doctorateRepository.findByUsername(n);
        if (d.isPresent()) return d.get().getId();
        return null;
    }
}