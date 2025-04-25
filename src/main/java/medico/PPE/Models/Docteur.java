package medico.PPE.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Enums.AppointmentTypeEnum;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Docteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "HollyDays")
    private String HollyDays;
    @Column(unique=true)
    private String email;
    @Column(name = "Tel")
    private String Tel;
    @Column(name = "professionalAddress")
    private String professionalAddress;
    @Column(name = "Licence")
    private String Licence;
    @Enumerated(EnumType.STRING)
    private AppointmentTypeEnum specialite;
    @Column(name = "numeroLicence")
    private String numeroLicence;
    @Column(name = "anneesExperience")
    private String anneesExperience;
    @Column(name = "photoUrl")
    private String photoUrl;
    @OneToMany(mappedBy ="docteur" ,cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Creneau> creneau;
    @Column(name = "cvurl")
    private String cvurl;
    @Column(name = "password")
    private String password;
    @Column(name = "confirmpassword")
    private String confirmpassword;
    // il va falloire mettre la specialité afin qu'elle soit disponible lors du rendez-vous

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHollyDays() {
        return HollyDays;
    }

    public void setHollyDays(String hollyDays) {
        HollyDays = hollyDays;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getProfessionalAddress() {
        return professionalAddress;
    }

    public void setProfessionalAddress(String professionalAddress) {
        this.professionalAddress = professionalAddress;
    }

    public String getLicence() {
        return Licence;
    }

    public void setLicence(String licence) {
        Licence = licence;
    }

    public AppointmentTypeEnum getSpecialite() {
        return specialite;
    }

    public void setSpecialite(AppointmentTypeEnum specialite) {
        this.specialite = specialite;
    }

    public String getNumeroLicence() {
        return numeroLicence;
    }

    public void setNumeroLicence(String numeroLicence) {
        this.numeroLicence = numeroLicence;
    }

    public String getAnneesExperience() {
        return anneesExperience;
    }

    public void setAnneesExperience(String anneesExperience) {
        this.anneesExperience = anneesExperience;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Creneau> getCreneau() {
        return creneau;
    }

    public void setCreneau(List<Creneau> creneau) {
        this.creneau = creneau;
    }

    public String getCvurl() {
        return cvurl;
    }

    public void setCvurl(String cvurl) {
        this.cvurl = cvurl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }
}
