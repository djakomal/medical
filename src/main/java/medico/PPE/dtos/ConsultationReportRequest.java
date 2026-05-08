package medico.PPE.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationReportRequest {
    private String title;
    private String content;
    private String diagnosis;
    private String treatment;
    private String prescription;
}

