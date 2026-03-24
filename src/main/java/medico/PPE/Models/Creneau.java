package medico.PPE.Models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Creneau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String jour;
    private String heureDebut;
    private String heureFin;
    private LocalDate date;
    private Boolean disponible;

    @ManyToOne
    @JoinColumn(name="docteur_id")
    @JsonIgnore
    @JsonManagedReference("creneau-docteur")
    private Docteur docteur;

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public Docteur getDocteur() {
        return docteur;
    }

    public void setDocteur(Docteur docteur) {
        this.docteur = docteur;
    }

    public void setDate(LocalDate date){
        this.date= date;
    }
    public LocalDate getDate(){
        return date;
    }

    public void setDisponible(Boolean disponible){
        this.disponible= disponible;
    }

    public boolean getDisponible(){
        return disponible;
    }
}