package medico.PPE.Services;

import medico.PPE.Models.Customer;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.dtos.SignupRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Customer createCustomer(SignupRequest signupRequest) {
        //Check if customer already exist
        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            return null;
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(signupRequest,customer);

        //Hash the password before saving
        String hashPassword = passwordEncoder.encode(signupRequest.getPassword());
        customer.setPassword(hashPassword);
        Customer createdCustomer = customerRepository.save(customer);
        customer.setId(createdCustomer.getId());
        return customer;
    }

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public void delete(Long Id) {
        /*Optional<Customer> optionalUser = userRepository.findById(Id);*/
        customerRepository.deleteById(Id);

    }
    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

}
