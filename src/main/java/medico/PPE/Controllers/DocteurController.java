package medico.PPE.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Models.Docteur;
import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.Services.UserDetailsServiceImpl;
import medico.PPE.dtos.LoginRequest;
import medico.PPE.dtos.LoginResponse;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

import java.io.IOException;

@Controller
@RestController
@RequestMapping("/docteur")
@CrossOrigin(origins = "http://localhost:4200")
public class DocteurController {

    private final AuthenticationManager authenticationManager;
    private final DoctorateServiceImpl docteurService;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl   userDetailsService;


    @Autowired
    public DocteurController(AuthenticationManager authenticationManager, DoctorateServiceImpl docteurService, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.docteurService = docteurService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        try {
            // ✅ Normaliser le username
            String normalizedUsername = loginRequest.getUsername().trim().toLowerCase();
            
            // ✅ Authentifier
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedUsername, loginRequest.getPassword())
            );
            
            // ✅ Charger les détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedUsername);
            
            // ✅ Récupérer l'ID du docteur
            Long userId = userDetailsService.getUserIdByUsername(normalizedUsername);
            
            if (userId == null) {
                System.out.println("❌ Impossible de récupérer l'ID pour le docteur: " + normalizedUsername);
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des informations utilisateur"));
            }
            
            System.out.println("✅ Docteur authentifié - Username: " + normalizedUsername + ", ID: " + userId);
            
            // ✅ Générer le token AVEC l'ID
            String jwt = jwtUtil.generateToken(normalizedUsername, userId);
            
            // ✅ Retourner le token ET l'ID
            return ResponseEntity.ok(new LoginResponse(jwt, userId));
            
        } catch (BadCredentialsException e) {
            System.out.println("❌ BadCredentialsException: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Email ou mot de passe incorrect"));
                
        } catch (DisabledException e) {
            System.out.println("❌ DisabledException: Compte désactivé");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Docteur non activé");
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Compte désactivé"));
                
        } catch (Exception e) {
            System.out.println("❌ Exception dans login docteur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erreur lors de la connexion: " + e.getMessage()));
        }
    }

    private static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
}
