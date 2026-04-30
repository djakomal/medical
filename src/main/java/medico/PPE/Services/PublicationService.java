package medico.PPE.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medico.PPE.Models.Docteur;
import medico.PPE.Models.Publication;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Repositories.PublicationRepository;

import java.time.LocalDate;
import java.util.Date;
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
        this.docteurRepository    = docteurRepository;
        this.publicationRepository = publicationRepository;
    }

    // ── Créer ──────────────────────────────────────────────
public Publication creerPublication(Publication publication, String username) {
    Docteur docteur = docteurRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
    publication.setDocteur(docteur);
    return publicationRepository.save(publication);
}

    // ── Lire tous ──────────────────────────────────────────
   // Ajouter dans PublicationService :
public List<Publication> getAllPublication(String username) {
    Docteur docteur = docteurRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
    return publicationRepository.findByDocteurId(docteur.getId());
}
public List<Publication> getPublicationPublies() {
    List<Publication> all = publicationRepository.findAll();
    System.out.println("Total publications: " + all.size());
    all.forEach(p -> System.out.println("ID: " + p.getId() + " | publie: " + p.getPublie()));
    
    List<Publication> publiees = publicationRepository.findByPublieTrue();
    System.out.println("Publications publiées: " + publiees.size());
    return publiees;
}

    // ── Lire par ID ────────────────────────────────────────
    public Publication getPublicationById(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Publication non trouvée avec l'ID: " + id));
        return publicationRepository.save(publication);
    }

    // ── Mettre à jour ──────────────────────────────────────
    public Publication updatePublication(Long id, Publication publicationData) {
        // 1. Charger l'entité existante (nom différent pour éviter la collision)
        Publication existing = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Publication non trouvée avec l'ID: " + id));

        // 2. Appliquer les modifications
        existing.setTitre(publicationData.getTitre());
        existing.setContenu(publicationData.getContenu());
        existing.setImageUrl(publicationData.getImageUrl());

        // 3. Conserver la date existante si le frontend n'en envoie pas
        if (publicationData.getDatePublication() != null) {
            existing.setDatePublication(publicationData.getDatePublication());
        } else if (existing.getDatePublication() == null) {
              existing.setDatePublication(LocalDate.now());
        }

        return publicationRepository.save(existing);
    }

    // ── Toggle publish ─────────────────────────────────────
    public Publication togglePublish(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Publication non trouvée avec l'ID: " + id));
        publication.setPublie(!publication.getPublie());
        return publicationRepository.save(publication);
    }

    // ── Supprimer ──────────────────────────────────────────
    public void deletePublication(Long id) {
        if (!publicationRepository.existsById(id)) {
            throw new RuntimeException("Publication non trouvée avec l'ID: " + id);
        }
        publicationRepository.deleteById(id);
    }
}