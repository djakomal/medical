package medico.PPE.Services;

import lombok.extern.slf4j.Slf4j;
import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Models.OtpValidation;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Repositories.OtpValidationRepository;
import medico.PPE.dtos.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ import correct
import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Slf4j
@Service
public class OtpValidationService {

    private final OtpValidationRepository validationRepository;
    private final NotificationService notificationService;
    private final CustomerRepository customerRepository;
    private final DoctorateRepository doctorateRepository;

    @Autowired
    public OtpValidationService(
            OtpValidationRepository validationRepository,
            NotificationService notificationService,
            CustomerRepository customerRepository,
             DoctorateRepository doctorateRepository
    ) {
        this.validationRepository = validationRepository;
        this.notificationService = notificationService;
        this.customerRepository = customerRepository;
        this.doctorateRepository = doctorateRepository;
        
    }

    @Transactional                                               // ✅ gardé dans la même transaction
    public void save(SignupRequest dto) {

        Customer user = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // ✅ Supprimer un OTP existant pour ce customer avant d'en créer un nouveau
        validationRepository.findByCustomer(user)
                .ifPresent(existing -> {
                    validationRepository.delete(existing);
                    validationRepository.flush();   // ✅ forcer la suppression avant l'insert
                });

        OtpValidation validation = new OtpValidation();
        validation.setCustomer(user);

        Instant creation = Instant.now();
        validation.setCreationOtp(creation);
        validation.setExpireOtp(creation.plus(3, ChronoUnit.MINUTES));

        String code = String.format("%06d", new Random().nextInt(999999));
        validation.setCode(code);

        validationRepository.save(validation);

        log.info("✅ OTP généré pour : {}", user.getEmail());
        notificationService.send(validation);
    }

  


    // OtpValidationService.java — ajouter cette méthode
@Transactional
public void saveForDocteur(Long docteurId) {

    Docteur docteur = doctorateRepository.findById(docteurId)
            .orElseThrow(() -> new EntityNotFoundException("Docteur introuvable"));

    validationRepository.findByDocteur(docteur)
            .ifPresent(existing -> {
                validationRepository.delete(existing);
                validationRepository.flush();
            });

    OtpValidation validation = new OtpValidation();
    validation.setDocteur(docteur);


    Instant creation = Instant.now();
    validation.setCreationOtp(creation);
    validation.setExpireOtp(creation.plus(3, ChronoUnit.MINUTES));
    validation.setCode(String.format("%06d", new Random().nextInt(999999)));

    validationRepository.save(validation);
    notificationService.send(validation);

    log.info("✅ OTP généré pour le docteur : {}", docteur.getEmail());
}



public OtpValidation readCode(String code) {
    if (code == null) {
        log.error("Le code OTP est null");
        return null;
    }
    return validationRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Your code is invalid"));
}

}