package medico.PPE.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoomUpdateDto {
    private String topic;
    private String startTime;
    private int duration;
    private String timezone;
    private boolean hostVideo;
    private boolean participantVideo;
}