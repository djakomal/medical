package medico.PPE.Services;


import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.dtos.DocteurDto;
import medico.PPE.dtos.DocteurResponse;
import medico.PPE.dtos.SignupRequest;


import java.util.List;

public interface AuthService {
    Customer createCustomer(SignupRequest signupRequest);

    List<Customer> getAll();



    void delete(Long Id);

    Customer getCustomerById(Long id);

    DocteurResponse createDocteur(Docteur docteur);

    List<Docteur> getAllDocteur();

    void deleteDocteur(Long Id);
}
