package medico.PPE.Services;

import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.CreneauRepository;
import medico.PPE.Repositories.DoctorateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class CreneauServiceImpl {
    
    @Autowired
    private CreneauRepository creneauRepository;
    
    @Autowired
    private DoctorateRepository doctorateRepository;
    
    public List<Creneau> getCreneauxByDocteur(Long docteurId) {
        return creneauRepository.findByDocteurId(docteurId);
    }
    
    public Creneau ajouterCreneau(Long docteurId, Creneau creneauRequest) {
        Docteur docteur = doctorateRepository.findById(docteurId)
            .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec l'ID : " + docteurId));
        Creneau creneau = new Creneau();
        creneau.setDate(creneauRequest.getDate());
        creneau.setHeureDebut(creneauRequest.getHeureDebut());
        creneau.setHeureFin(creneauRequest.getHeureFin());
        creneau.setDisponible(true);
        creneau.setDocteur(docteur); // setDocteur
        
        return creneauRepository.save(creneau);
    }
    
    public Creneau modifierCreneau(Long docteurId, Long creneauId, Creneau creneauRequest) {
        Creneau creneau = creneauRepository.findById(creneauId)
            .orElseThrow(() -> new RuntimeException("Créneau non trouvé"));
        
        if (!creneau.getDocteur().getId().equals(docteurId)) {
            throw new RuntimeException("Vous n'avez pas l'autorisation de modifier ce créneau");
        }
        
        creneau.setDate(creneauRequest.getDate());
        creneau.setHeureDebut(creneauRequest.getHeureDebut());
        creneau.setHeureFin(creneauRequest.getHeureFin());
        
        return creneauRepository.save(creneau);
    }
    
    public void supprimerCreneau(Long docteurId, Long creneauId) {
        Creneau creneau = creneauRepository.findById(creneauId)
            .orElseThrow(() -> new RuntimeException("Créneau non trouvé"));
        
        if (!creneau.getDocteur().getId().equals(docteurId)) {
            throw new RuntimeException("Vous n'avez pas l'autorisation de supprimer ce créneau");
        }
        
        creneauRepository.deleteById(creneauId);
    }
    
    public List<Creneau> getCreneauxDisponibles(Long docteurId) {
        return creneauRepository.findByDocteurIdAndDisponibleTrue(docteurId);
    }



    public boolean estDisponible(Long creneauId) {
        Optional<Creneau> creneauOpt = creneauRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            throw new RuntimeException("Créneau non trouvé");
        }
        Creneau creneau = creneauOpt.get();
        
        // Vérifier si disponible ET si la date n'est pas passée
        return creneau.getDisponible() && !estDatePassee(creneau);
    }

    public Creneau marquerIndisponible(Long creneauId) {
        Optional<Creneau> creneauOpt = creneauRepository.findById(creneauId);
        if (creneauOpt.isEmpty()) {
            throw new RuntimeException("Créneau non trouvé");
        }
        
        Creneau creneau = creneauOpt.get();
        if (!creneau.getDisponible()) {
            throw new RuntimeException("Créneau déjà indisponible");
        }
        
        creneau.setDisponible(false);
        return creneauRepository.save(creneau);
    }

    
    private boolean estDatePassee(Creneau creneau) {
        try {
            // Récupérer les chaînes de caractères
            LocalDate dateStr = creneau.getDate();      
            String heureStr = creneau.getHeureDebut(); 
            
            // Validation basique
            if (
                heureStr == null || heureStr.trim().isEmpty()) {
                return true; // Données invalides, considérer comme passé
            }
            
            // Nettoyer les chaînes
            heureStr = heureStr.trim();
            
            // Parser l'heure (gérer différents formats)
            LocalTime heure;
            if (heureStr.length() >= 5) {
                // Prendre les 5 premiers caractères "HH:mm"
                heure = LocalTime.parse(heureStr.substring(0, 5));
            } else {
                // Format court
                heure = LocalTime.parse(heureStr);
            }
            
            // Combiner date et heure
            LocalDateTime dateTimeCreneau = LocalDateTime.of(dateStr, heure);
            
            // Obtenir la date/heure actuelle
            LocalDateTime maintenant = LocalDateTime.now();
            
            // Comparer
            return dateTimeCreneau.isBefore(maintenant);
            
        } catch (DateTimeParseException e) {
            System.err.println("❌ Erreur de parsing pour le créneau ID " + creneau.getId() + 
                             ": date='" + creneau.getDate() + 
                             "', heure='" + creneau.getHeureDebut() + "'");
            return true; // En cas d'erreur, considérer comme passé
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue dans estDatePassee: " + e.getMessage());
            return true;
        }
    }
}