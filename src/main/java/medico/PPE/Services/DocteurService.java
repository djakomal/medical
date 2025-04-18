package medico.PPE.Services;

import medico.PPE.Models.Docteur;

import java.util.List;
import java.util.Optional;

public interface DocteurService {
    Docteur add(Docteur docteur);

    List<Docteur> getAllDocteur();

    Optional<Docteur> getById(long Id);

    void deleteDocteur(Long Id);
}
