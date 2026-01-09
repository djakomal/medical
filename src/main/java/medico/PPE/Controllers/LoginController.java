package medico.PPE.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Services.UserDetailsServiceImpl;
import medico.PPE.dtos.LoginRequest;
import medico.PPE.dtos.LoginResponse;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
// @CrossOrigin(origins = "http://localhost:4200")

public class LoginController {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, 
                          UserDetailsServiceImpl userDetailsService, 
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // ✅ Normaliser le username
            String normalizedUsername = loginRequest.getUsername().trim().toLowerCase();
            
            // ✅ Authentifier avec Spring Security
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedUsername, loginRequest.getPassword())
            );
            
            // ✅ Charger les détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedUsername);
            
            // ✅ Générer le token JWT
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            
            // ✅ Retourner le token
            return ResponseEntity.ok(new LoginResponse(jwt));
            
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Nom d'utilisateur incorrect"));
                
        } catch (BadCredentialsException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Mot de passe incorrect"));
                
        } catch (DisabledException e) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Compte désactivé"));
                
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erreur lors de la connexion"));
        }
    }
    
    // DTO pour les erreurs
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