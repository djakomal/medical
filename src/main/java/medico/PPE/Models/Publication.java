
package medico.PPE.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "publication")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;
    
    @Column(nullable = false)
    private LocalDate datePublication;
    
    @Column(length = 10485760)
    private String imageUrl;

    private Boolean publie = false;
    @PrePersist
    protected void onCreate() {
        if (datePublication == null) {
            datePublication = LocalDate.now();
        }
    }
}