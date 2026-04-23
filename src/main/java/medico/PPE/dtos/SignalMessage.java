package medico.PPE.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalMessage {
    private String type;      // "offer" | "answer" | "ice-candidate"
    private String payload;   // SDP ou ICE candidate en JSON string
    private String roomId;
    private String senderId;
}