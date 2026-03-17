package medico.PPE.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Enums.AppointmentTypeEnum;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Docteur implements UserDetails {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @Column(name = "holly_days")  // Utilisez le style snake_case pour SQL
        private String hollyDays;

        @Column(unique = true, nullable = false)  // Garantit l'unicité et la non-nullité
        private String email;

        private String tel;
        @Column(name = "username", unique = true, nullable = false)
         private String username;

        @Column(name = "professional_address")
        private String professionalAddress;

        private String licence;

        @Enumerated(EnumType.STRING)
        private AppointmentTypeEnum specialite;

        @Column(name = "numero_licence")
        private String numeroLicence;

        @Column(name = "annees_experience")
        private String anneesExperience;

        @Column(name = "photo_url")
        private String photoUrl;
        @JsonIgnore
        @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, 
        orphanRemoval = true, fetch = FetchType.EAGER)
        private List<Creneau> creneau;

        @Column(name = "cvurl")
        private String cvurl;

        @Column(nullable = false)
        private String password;

        @Transient  // Ne pas persister cette propriété
        private String confirmpassword;




            // Implémentation UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTEUR"));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Column(nullable = false)
    private boolean enabled = false;
    
    @Override
    public boolean isEnabled() {
        return this.enabled;           // ← utiliser le vrai champ
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;        // ← plus d'exception
    }

}
