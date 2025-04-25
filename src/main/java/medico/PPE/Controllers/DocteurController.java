package medico.PPE.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Services.DocteurService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RestController
@RequestMapping("/docteur")
@CrossOrigin(origins = "http://localhost:4200")
public class DocteurController {

    private final AuthenticationManager authenticationManager;
    private final  DocteurService docteurService;;
    private final JwtUtil jwtUtil;


    @Autowired
    public DocteurController( AuthenticationManager authenticationManager1, DocteurService docteurService, JwtUtil jwtUtil1) {
        this.authenticationManager = authenticationManager1;
        this.docteurService = docteurService;
        this.jwtUtil = jwtUtil1;
    }

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect email or password.");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer is not activated");
            return null;
        }

        final UserDetails userDetails = docteurService.loadUserByUsername(loginRequest.getEmail());



        // Générer un JWT avec le rôle de l'utilisateur
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // Retourner le token JWT et le rôle
        return new LoginResponse(jwt);
    }


}
