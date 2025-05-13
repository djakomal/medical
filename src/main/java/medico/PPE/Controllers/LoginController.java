package medico.PPE.Controllers;


import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.dtos.LoginRequest;
import medico.PPE.dtos.LoginResponse;
import medico.PPE.jwt.CustomerServiceImpl;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final CustomerServiceImpl customerService;
    private final JwtUtil jwtUtil;


    @Autowired
    public LoginController(AuthenticationManager authenticationManager, CustomerServiceImpl customerService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect email or password.");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer is not activated");
            return null;
        }

        final UserDetails userDetails = customerService.loadUserByUsername(loginRequest.getEmail());



        // Générer un JWT avec le rôle de l'utilisateur
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // Retourner le token JWT et le rôle
        return new LoginResponse(jwt);
    }


    // Méthode pour obtenir le rôle de l'utilisateur (ici, basé sur votre implémentation)

}
