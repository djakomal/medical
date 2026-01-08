package medico.PPE.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConseilDto {
    private Long id;
    private String titre;
    private String contenu;
    private String auteur;
    private LocalDate datePublication;
    private String imageUrl;
    private List<String> tags;
    private String categorie;
    private Boolean publie;
    private Integer nombreVues;
}