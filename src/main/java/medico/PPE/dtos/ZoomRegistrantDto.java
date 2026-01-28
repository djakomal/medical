package medico.PPE.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoomRegistrantDto {
    private String email;
    private String firstName;
    private String lastName;
    private String meetingId;

}