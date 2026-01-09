package medico.PPE.Services;

import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.DoctorateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class DoctorateServiceImpl {
    
    @Autowired
    private DoctorateRepository doctorateRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Ajouter un nouveau docteur avec validation des créneaux
     */
    public Docteur add(Docteur docteur) {
        // ✅ Validation des champs obligatoires
        if (docteur.getUsername() == null || docteur.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        if (docteur.getEmail() == null || docteur.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        String normalizedUsername = docteur.getUsername().trim().toLowerCase();
        String normalizedEmail = docteur.getEmail().trim().toLowerCase();

        // ✅ Vérifier si le username existe déjà
        if (userDetailsService.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
        }

        // ✅ Vérifier si l'email existe déjà
        if (userDetailsService.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        // Normaliser les données
        docteur.setUsername(normalizedUsername);
        docteur.setEmail(normalizedEmail);

        // ✅ Validation des créneaux
        List<Creneau> creneaux = docteur.getCreneau();
        if (creneaux != null && !creneaux.isEmpty()) {
            Set<String> uniqueCreneaux = new HashSet<>();
            // Expression régulière pour valider le format "HH:mm"
            Pattern timePattern = Pattern.compile("^([01]?\\d|2[0-3]):([0-5]\\d)$");

            for (Creneau c : creneaux) {
                // Validation du format d'heure
                if (!timePattern.matcher(c.getHeureDebut()).matches() || 
                    !timePattern.matcher(c.getHeureFin()).matches()) {
                    throw new IllegalArgumentException(
                        "Le format d'heure est invalide: " + c.getHeureDebut() + " - " + c.getHeureFin()
                    );
                }

                // Vérification des doublons basés sur jour-heureDebut-heureFin
                String key = c.getJour() + "-" + c.getHeureDebut() + "-" + c.getHeureFin();
                if (!uniqueCreneaux.add(key)) {
                    throw new IllegalArgumentException("Créneau en doublon trouvé: " + key);
                }

                // Lier le créneau au docteur
                c.setDocteur(docteur);
            }
        }

        Docteur savedDocteur = doctorateRepository.save(docteur);
        System.out.println("✅ Docteur créé: " + savedDocteur.getUsername());
        return savedDocteur;
    }


    /**
     * Récupérer un docteur par ID
     */
    public Optional<Docteur> getById(Long id) {
        return doctorateRepository.findById(id);
    }

    /**
     * Récupérer un docteur par username
     */
    public Optional<Docteur> getByUsername(String username) {
        String normalizedUsername = username.trim().toLowerCase();
        return doctorateRepository.findByUsername(normalizedUsername);
    }

    /**
     * Récupérer un docteur par email
     */
    public Optional<Docteur> getByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return doctorateRepository.findByEmail(normalizedEmail);
    }

    /**
     * Supprimer un docteur
     */
    public void deleteDocteur(Long id) {
        doctorateRepository.deleteById(id);
    }

    /**
     * Mettre à jour un docteur
    //  */
    // public Docteur updateDocteur(Long id, Docteur docteurUpdate) {
    //     Docteur existingDocteur = doctorateRepository.findById(id)
    //         .orElseThrow(() -> new IllegalArgumentException("Docteur non trouvé avec l'ID: " + id));

    //     // Mettre à jour les champs si fournis
    //     if (docteurUpdate.getNom() != null) {
    //         existingDocteur.setNom(docteurUpdate.getNom());
    //     }
    //     if (docteurUpdate.getPrenom() != null) {
    //         existingDocteur.setPrenom(docteurUpdate.getPrenom());
    //     }
    //     if (docteurUpdate.getSpecialite() != null) {
    //         existingDocteur.setSpecialite(docteurUpdate.getSpecialite());
    //     }
    //     if (docteurUpdate.getEmail() != null) {
    //         String normalizedEmail = docteurUpdate.getEmail().trim().toLowerCase();
    //         // Vérifier si le nouvel email n'est pas déjà utilisé par un autre compte
    //         if (!existingDocteur.getEmail().equals(normalizedEmail) && 
    //             userDetailsService.existsByEmail(normalizedEmail)) {
    //             throw new IllegalArgumentException("Cet email est déjà utilisé");
    //         }
    //         existingDocteur.setEmail(normalizedEmail);
    //     }
    //     if (docteurUpdate.getUsername() != null) {
    //         String normalizedUsername = docteurUpdate.getUsername().trim().toLowerCase();
    //         // Vérifier si le nouveau username n'est pas déjà utilisé
    //         if (!existingDocteur.getUsername().equals(normalizedUsername) && 
    //             userDetailsService.existsByUsername(normalizedUsername)) {
    //             throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
    //         }
    //         existingDocteur.setUsername(normalizedUsername);
    //     }

    //     // Mettre à jour les créneaux si fournis
    //     if (docteurUpdate.getCreneau() != null) {
    //         existingDocteur.getCreneau().clear();
    //         for (Creneau c : docteurUpdate.getCreneau()) {
    //             c.setDocteur(existingDocteur);
    //             existingDocteur.getCreneau().add(c);
    //         }
    //     }

    //     return doctorateRepository.save(existingDocteur);
    // }

    /**
     * Vérifier si un docteur existe par email
     */
    public boolean existsByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return doctorateRepository.existsByEmail(normalizedEmail);
    }

    /**
     * Vérifier si un docteur existe par username
     */
    public boolean existsByUsername(String username) {
        String normalizedUsername = username.trim().toLowerCase();
        return doctorateRepository.existsByUsername(normalizedUsername);
    }
}