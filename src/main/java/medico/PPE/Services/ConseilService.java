package medico.PPE.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medico.PPE.Models.Conseil;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.ConseilRepository;
import medico.PPE.Repositories.DoctorateRepository;

import java.util.List;

@Service
@Transactional
public class ConseilService {
    
    private final ConseilRepository conseilRepository;
    private final DoctorateRepository docteurRepository;

    @Autowired
    public ConseilService(DoctorateRepository docteurRepository, ConseilRepository conseilRepository) {
        this.docteurRepository = docteurRepository;
        this.conseilRepository = conseilRepository;
    }
    
    // Créer un nouveau conseil
    public Conseil creerConseil(Conseil conseil, String username) {
        Docteur docteur = docteurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        conseil.setDocteur(docteur);
        conseil.setAuteur(docteur.getName());
        return conseilRepository.save(conseil);
    }
    
    // Récupérer tous les conseils
    public List<Conseil> getAllConseils() {
        return conseilRepository.findAll();
    }
    
    // Récupérer les conseils publiés
    public List<Conseil> getConseilsPublies() {
        return conseilRepository.findByPublieTrue();
    }
    
    // Récupérer un conseil par ID
    public Conseil getConseilById(Long id) {
        Conseil conseil = conseilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé avec l'ID: " + id));
        
        // Incrémenter le nombre de vues
        conseil.setNombreVues(conseil.getNombreVues() + 1);
        return conseilRepository.save(conseil);
    }
    
    // Récupérer les conseils par catégorie
    public List<Conseil> getConseilsByCategorie(String categorie) {
        return conseilRepository.findByCategorie(categorie);
    }
    
    // Récupérer les conseils par auteur
    public List<Conseil> getConseilsByAuteur(String auteur) {
        return conseilRepository.findByAuteur(auteur);
    }
    
    // Rechercher des conseils par titre
    public List<Conseil> rechercherConseils(String query) {
        return conseilRepository.findByTitreContainingIgnoreCase(query);
    }
    
    // Mettre à jour un conseil
    public Conseil updateConseil(Long id, Conseil conseilData) {
        Conseil conseil = conseilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé avec l'ID: " + id));
        
        conseil.setTitre(conseilData.getTitre());
        conseil.setContenu(conseilData.getContenu());
        conseil.setDatePublication(conseilData.getDatePublication());
        conseil.setImageUrl(conseilData.getImageUrl());
        conseil.setTags(conseilData.getTags());
        conseil.setCategorie(conseilData.getCategorie());
        conseil.setPublie(conseilData.getPublie());
        
        return conseilRepository.save(conseil);
    }
    
    // Publier/Dépublier un conseil
    public Conseil togglePublish(Long id) {
        Conseil conseil = conseilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé avec l'ID: " + id));
        
        conseil.setPublie(!conseil.getPublie());
        return conseilRepository.save(conseil);
    }
    
    // Supprimer un conseil
    public void deleteConseil(Long id) {
        if (!conseilRepository.existsById(id)) {
            throw new RuntimeException("Conseil non trouvé avec l'ID: " + id);
        }
        conseilRepository.deleteById(id);
    }

    // Récupérer les conseils par docteur
    public List<Conseil> getConseilsByDocteur(Long docteurId) {
        return conseilRepository.findByDocteurId(docteurId);
    }
}