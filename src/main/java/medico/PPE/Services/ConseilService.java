package medico.PPE.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medico.PPE.Models.Conseil;
import medico.PPE.Repositories.ConseilRepository;
import medico.PPE.dtos.ConseilDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConseilService {
    
    @Autowired
    private ConseilRepository conseilRepository;
    
    // Créer un nouveau conseil
    public ConseilDto creerConseil(ConseilDto conseilDTO) {
        Conseil conseil = convertToEntity(conseilDTO);
        Conseil savedConseil = conseilRepository.save(conseil);
        return convertToDTO(savedConseil);
    }
    
    // Récupérer tous les conseils
    public List<ConseilDto> getAllConseils() {
        return conseilRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Récupérer les conseils publiés
    public List<ConseilDto> getConseilsPublies() {
        return conseilRepository.findByPublieTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Récupérer un conseil par ID
    public ConseilDto getConseilById(Long id) {
        Conseil conseil = conseilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé avec l'ID: " + id));
        
        // Incrémenter le nombre de vues
        conseil.setNombreVues(conseil.getNombreVues() + 1);
        conseilRepository.save(conseil);
        
        return convertToDTO(conseil);
    }
    
    // Récupérer les conseils par catégorie
    public List<ConseilDto> getConseilsByCategorie(String categorie) {
        return conseilRepository.findByCategorie(categorie).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Récupérer les conseils par auteur
    public List<ConseilDto> getConseilsByAuteur(String auteur) {
        return conseilRepository.findByAuteur(auteur).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Rechercher des conseils par titre
    public List<ConseilDto> rechercherConseils(String query) {
        return conseilRepository.findByTitreContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Mettre à jour un conseil
    public ConseilDto updateConseil(Long id, ConseilDto conseilDTO) {
        Conseil conseil = conseilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé avec l'ID: " + id));
        
        conseil.setTitre(conseilDTO.getTitre());
        conseil.setContenu(conseilDTO.getContenu());
        conseil.setAuteur(conseilDTO.getAuteur());
        conseil.setImageUrl(conseilDTO.getImageUrl());
        conseil.setTags(conseilDTO.getTags());
        conseil.setCategorie(conseilDTO.getCategorie());
        conseil.setPublie(conseilDTO.getPublie());
        
        Conseil updatedConseil = conseilRepository.save(conseil);
        return convertToDTO(updatedConseil);
    }
    
    // Publier/Dépublier un conseil
    public ConseilDto togglePublish(Long id) {
        Conseil conseil = conseilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conseil non trouvé avec l'ID: " + id));
        
        conseil.setPublie(!conseil.getPublie());
        Conseil updatedConseil = conseilRepository.save(conseil);
        return convertToDTO(updatedConseil);
    }
    
    // Supprimer un conseil
    public void deleteConseil(Long id) {
        if (!conseilRepository.existsById(id)) {
            throw new RuntimeException("Conseil non trouvé avec l'ID: " + id);
        }
        conseilRepository.deleteById(id);
    }
    
    // Conversion Entity -> DTO
    private ConseilDto convertToDTO(Conseil conseil) {
        return new ConseilDto(
                conseil.getId(),
                conseil.getTitre(),
                conseil.getContenu(),
                conseil.getAuteur(),
                conseil.getDatePublication(),
                conseil.getImageUrl(),
                conseil.getTags(),
                conseil.getCategorie(),
                conseil.getPublie(),
                conseil.getNombreVues()
        );
    }
    
    // Conversion DTO -> Entity
    private Conseil convertToEntity(ConseilDto dto) {
        Conseil conseil = new Conseil();
        conseil.setId(dto.getId());
        conseil.setTitre(dto.getTitre());
        conseil.setContenu(dto.getContenu());
        conseil.setAuteur(dto.getAuteur());
        conseil.setDatePublication(dto.getDatePublication());
        conseil.setImageUrl(dto.getImageUrl());
        conseil.setTags(dto.getTags());
        conseil.setCategorie(dto.getCategorie());
        conseil.setPublie(dto.getPublie() != null ? dto.getPublie() : false);
        conseil.setNombreVues(dto.getNombreVues() != null ? dto.getNombreVues() : 0);
        return conseil;
    }
}