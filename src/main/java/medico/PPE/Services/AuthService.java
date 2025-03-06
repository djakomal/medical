package medico.PPE.Services;


import medico.PPE.Models.Customer;
import medico.PPE.dtos.SignupRequest;

import java.util.List;

public interface AuthService {
    Customer createCustomer(SignupRequest signupRequest);

    List<Customer> getAll();

    void delete(Long Id);

    Customer getCustomerById(Long id);
}
