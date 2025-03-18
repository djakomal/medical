package medico.PPE.Models;

import jakarta.persistence.*;
import medico.PPE.Enums.AppointmentTypeEnum;

@Entity
@Table(name = "AppointmentType")

public class AppointementType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppointmentTypeEnum getType() {
        return type;
    }

    public void setType(AppointmentTypeEnum type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    private AppointmentTypeEnum type=AppointmentTypeEnum.GENERAL;;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;
    @Column(name = "date")
    private String date;
    @Column(name = "heure")
    private String heure;

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
