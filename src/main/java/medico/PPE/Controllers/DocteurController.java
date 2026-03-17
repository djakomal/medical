package medico.PPE.Controllers;

import medico.PPE.Services.AuthService;
import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.Services.UserDetailsServiceImpl;
import medico.PPE.dtos.LoginRequest;
import medico.PPE.dtos.LoginResponse;
import medico.PPE.utils.JwtUtil;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@Slf4j   
@RestController                             
@RequestMapping("/docteur")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173"})
public class DocteurController {

    private final AuthenticationManager authenticationManager;
    private final DoctorateServiceImpl docteurService;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthService authService;  

    @Autowired
    public DocteurController(AuthenticationManager authenticationManager,
                             DoctorateServiceImpl docteurService,
                             JwtUtil jwtUtil,
                             UserDetailsServiceImpl userDetailsService,
                             AuthService authService
                            ) {
        this.authenticationManager = authenticationManager;
        this.docteurService = docteurService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String normalizedUsername = loginRequest.getUsername().trim().toLowerCase();

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedUsername, loginRequest.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedUsername);
            Long userId = userDetailsService.getUserIdByUsername(normalizedUsername);
            String jwt = jwtUtil.generateToken(normalizedUsername, userId);

            return ResponseEntity.ok(new LoginResponse(jwt, userId));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email ou mot de passe incorrect"));

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Compte non activé. Vérifiez votre email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la connexion : " + e.getMessage()));
        }
    }
    private static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError()           { return error; }
        public void setError(String error) { this.error = error; }
    }
}