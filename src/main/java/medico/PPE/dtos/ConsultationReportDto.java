package medico.PPE.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationReportDto {
    private Long id;
    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private String title;
    private String content;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

