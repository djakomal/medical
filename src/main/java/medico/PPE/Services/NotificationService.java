package medico.PPE.Services;

import lombok.extern.slf4j.Slf4j;
import medico.PPE.Models.OtpValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(OtpValidation validation) {
        // ✅ Déterminer email et nom selon le type d'utilisateur
        String email;
        String nom;

        if (validation.getDocteur() != null) {
            email = validation.getDocteur().getEmail();
            nom   = validation.getDocteur().getUsername();
        } else {
            email = validation.getCustomer().getEmail();
            nom   = validation.getCustomer().getUsername();
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("MEDICO");
        mailMessage.setTo(email);
        mailMessage.setSubject("Your activation code");
        mailMessage.setText(String.format(
            "Hello %s, your activation code is: %s",
            nom,
            validation.getCode()
        ));

        javaMailSender.send(mailMessage);
        log.info("✅ Email envoyé à : {}", email);
    }
}