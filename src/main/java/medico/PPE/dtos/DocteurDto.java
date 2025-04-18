package medico.PPE.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import medico.PPE.Enums.AppointmentTypeEnum;
import medico.PPE.Models.Creneau;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class DocteurDto {
    @Column(name = "name")
    private String name;
    @Column(name = "HollyDays")
    private String HollyDays;
    @Column(unique=true,nullable =false)
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

    private List<Creneau> creneau;
    @Column(name = "cvurl")
    private String cvurl;
    @Column(name = "password")
    private String password;
    @Column(name = "confirmpassword")
    private String confirmpassword;
}
