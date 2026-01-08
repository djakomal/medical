package medico.PPE.Services;
import medico.PPE.Models.Docteur;
import medico.PPE.dtos.DocteurSignupRequest;


import java.util.List;
import java.util.Optional;

public interface DoctorateService {
    Docteur add(Docteur docteur);
    List<Docteur>getAllDocteurs();
    Docteur getById(Long Id);
    void deleteDocteur(Long Id);
    List<Docteur> getAllDocteur();
    Optional<Docteur> getById(long Id);
    Docteur updateDocteur(Long id, Docteur docteur);
    Optional<Docteur> findByEmail(String email);

}
