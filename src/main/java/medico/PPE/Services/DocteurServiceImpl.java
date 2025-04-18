package medico.PPE.Services;

import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.DocteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class DocteurServiceImpl implements DocteurService{
    @Autowired
    private  DocteurRepository docteurRepository;
    @Autowired
    public DocteurService docteurService;
    @Override
    public Docteur add(Docteur docteur){
        if(docteurRepository.existsByEmail(docteur.getEmail())){
            throw new IllegalArgumentException("un compet existe deja vec cet email");
        }
        List<Creneau> creneau =docteur.getCreneau();
        if(creneau!=null &&!creneau.isEmpty()){
            Set<String> uniqueCreneau= new HashSet<>();
            // exception reguiliere pour valider le format "HH:mm"
            Pattern timePattern= Pattern.compile("^([01]?\\d|2[0-3]):([0-5]\\d)$");

            for(Creneau c :creneau){
                //validation duu formation d'heure
                if(!timePattern.matcher(c.getHeureDebut()).matches()||!timePattern.matcher(c.getHeureFin()).matches()){
                    throw new IllegalArgumentException("Le forma d'heure est invalide" +c.getHeureDebut() +"-"+c.getHeureFin());
                }
                // Verification des doublons basé sur jour-heureDebu-heureFin
                String key=c.getJour()+"-"+c.getHeureDebut()+"-"+c.getHeureFin();
                if(!uniqueCreneau.add(key)){
                    throw new IllegalArgumentException("Creneau en doublons trouvé:"+key);
                }
                // lister le creneau aux medecin
                c.setDocteur(docteur);
            }
        }
                return docteurRepository.save(docteur);
    }
    @Override
    public List<Docteur>getAllDocteur(){
        return docteurRepository.findAll();
    }
    @Override
    public Optional<Docteur> getById(long Id){
        return docteurRepository.findById(Id);
    }
    @Override
    public void deleteDocteur(Long Id){
        docteurRepository.deleteById(Id);
    }
}
