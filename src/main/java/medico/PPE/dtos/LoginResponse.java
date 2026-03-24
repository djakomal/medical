package medico.PPE.dtos;

public record LoginResponse(String jwt,Long userId, String role) {}
