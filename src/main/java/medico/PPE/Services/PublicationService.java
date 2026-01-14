package medico.PPE.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medico.PPE.Models.Publication;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Repositories.PublicationRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublicationService {
    
    private final PublicationRepository publicationRepository;

    private final DoctorateRepository docteurRepository;

    @Autowired
    public PublicationService(
        DoctorateRepository docteurRepository,
         PublicationRepository publicationRepository) {
        this.docteurRepository = docteurRepository;
        this.publicationRepository = publicationRepository;
    }
    
    // Créer un nouveau conseil

    public Publication creerPublication(Publication publication) {

        // Docteur docteur = docteurRepository.findByUsername(docteurId)
        // .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        // Publication publication = convertToEntity(publication);
        // publication.setDocteur(docteur);

        Publication savedPublication = publicationRepository.save(publication);
        return savedPublication;
    }

        // Récupérer tous les conseils
    public List<Publication> getAllPublication() {
        return publicationRepository.findAll().stream()
                .collect(Collectors.toList());
    }

    // Récupérer les publications publiées
    public List<Publication> getPublicationPublies() {
        return publicationRepository.findByPublieTrue().stream()
                .collect(Collectors.toList());
    }
    
    // Récupérer un conseil par ID
    public Publication getPublicationById(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'ID: " + id));
        
        // Incrémenter le nombre de vues
        publicationRepository.save(publication);
        
        return publication;
    }
    
    // // Mettre à jour un conseil
    // public Publication updatePublication(Long id, Publication publication) {
    //     Publication publication = publicationRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'ID: " + id));
    //     publication.setTitre(publication.getTitre());
    //     publication.setContenu(publication.getContenu());
    //     publication.setDatePublication(publication.getDatePublication());
    //     publication.setImageUrl(publication.getImageUrl())
    //     Publication updatedPublication = publicationRepository.save(publication);
    //     return updatedPublication;
    // }
    
    // Publier/Dépublier un conseil
    public Publication togglePublish(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'ID: " + id));
        publication.setPublie(!publication.getPublie());
        Publication updatedPublication = publicationRepository.save(publication);
        return updatedPublication;
    }
    
    // Supprimer un conseil
    public void deletePublication(Long id) {
        if (!publicationRepository.existsById(id)) {
            throw new RuntimeException("Publication non trouvée avec l'ID: " + id);
        }
        publicationRepository.deleteById(id);
    }
    
}