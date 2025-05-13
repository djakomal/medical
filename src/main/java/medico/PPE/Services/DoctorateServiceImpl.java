package medico.PPE.Services;

import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.DoctorateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class DoctorateServiceImpl implements UserDetailsService {
    @Autowired
    private DoctorateRepository doctorateRepository;

   /* @Override
    public Docteur add(Docteur docteur){
        if(doctorateRepository.existsByEmail(docteur.getEmail())){
            throw new IllegalArgumentException("un compet existe deja avec cet email");
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
                return doctorateRepository.save(docteur);
    }*/
/*    @Override
    public List<Docteur>getAllDocteur(){
        return doctorateRepository.findAll();
    }
    @Override
    public Optional<Docteur> getById(long Id){
        return doctorateRepository.findById(Id);
    }
    @Override
    public void deleteDocteur(Long Id){
        doctorateRepository.deleteById(Id);
    }*/



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Si l'email contient "00" mais pas "@", essayez de le corriger
        if (email.contains("00") && !email.contains("@")) {
            String correctedEmail = email.replace("00", "@");
            System.out.println("Email corrigé: " + correctedEmail);

            try {
                Docteur d = doctorateRepository.findByEmail(correctedEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("Docteur non trouvé: " + correctedEmail));
                return new User(d.getEmail(), d.getPassword(), new ArrayList<>());
            } catch (UsernameNotFoundException e) {
                // Si l'email corrigé ne fonctionne pas, continuez avec l'email original
            }
        }

        Docteur d = doctorateRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Docteur non trouvé: " + email));

        return new User(d.getEmail(), d.getPassword(), new ArrayList<>());
    }
}
