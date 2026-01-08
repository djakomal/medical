package medico.PPE.Services;

import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.CreneauRepository;
import medico.PPE.Repositories.DoctorateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
        creneau.setDocteur(docteur); // ✅ setDocteur
        
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
}