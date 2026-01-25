package medico.PPE.dtos;


import java.util.List;

public record ZoomResponse(
        String id,
        String topic,
        String startTime,
        String joinUrl,
        String startUrl
) {}
