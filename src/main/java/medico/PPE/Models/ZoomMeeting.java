package medico.PPE.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "zoom_meetings")
public class ZoomMeeting {

    @Id
    private String id;

    private String topic;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "join_url", length = 1024)
    private String joinUrl;

    @Column(name = "start_url", length = 1024)
    private String startUrl;
}

