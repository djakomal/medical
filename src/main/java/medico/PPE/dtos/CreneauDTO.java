package medico.PPE.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreneauDTO {
    private Long id;
    private String jour;
    private String heureDebut;
    private String heureFin;
    private Long docteurId;
    private Boolean actif;
}