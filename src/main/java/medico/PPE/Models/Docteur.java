package medico.PPE.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Enums.AppointmentTypeEnum;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Docteur {


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

        @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Creneau> creneau;

        @Column(name = "cvurl")
        private String cvurl;

        @Column(nullable = false)
        private String password;

        @Transient  // Ne pas persister cette propriété
        private String confirmpassword;

        public String getDatePublication() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getDatePublication'");
        }

}
