
package medico.PPE.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "conseils")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conseil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(nullable = false)
    private String auteur;
    
    @Column(nullable = false)
    private LocalDate datePublication;
    
    @Column(nullable = true)
    private String imageUrl;
    
    @ElementCollection
    @CollectionTable(name = "conseil_tags", joinColumns = @JoinColumn(name = "conseil_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    @Column(nullable = false)
    private String categorie; // NUTRITION, BIEN-ÊTRE, VIRUS ET ÉPIDÉMIES, etc.
    
    @Column(nullable = false)
    private Boolean publie = false;
    
    @Column
    private Integer nombreVues = 0;
    
    @PrePersist
    protected void onCreate() {
        if (datePublication == null) {
            datePublication = LocalDate.now();
        }
    }
}