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
        mailMessage.setSubject("Code dactivation");
        mailMessage.setText(String.format(
            "Hello %s, votre code d'activation: %s",
            nom,
            validation.getCode()
        ));

        javaMailSender.send(mailMessage);
        log.info(" Email envoyé à : {}", email);
    }


    public void sendZoomLink(String emailPatient, String prenomPatient,
        String nomDocteur, String joinUrl)
         {
            try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("MEDICO");
            mailMessage.setTo(emailPatient);
            mailMessage.setSubject("Votre consultation en ligne est prête");
            mailMessage.setText(String.format("""
            Bonjour %s,

            Votre consultation avec Dr %s est maintenant prête.

            Rejoindre la consultation :
            %s

            Aucun compte Zoom n'est requis.
            Cliquez sur le lien, entrez votre prénom
            et attendez que le médecin vous admette.

            Cordialement,
            L'équipe Medico
            """, prenomPatient, nomDocteur, joinUrl));

            javaMailSender.send(mailMessage);
            log.info(" Lien Zoom envoyé à : {}", emailPatient);

            } catch (Exception e) {
            log.error("⚠️ Échec envoi email Zoom à {} : {}", emailPatient, e.getMessage());
            }
        }
    
            
}