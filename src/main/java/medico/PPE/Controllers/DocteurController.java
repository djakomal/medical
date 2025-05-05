package medico.PPE.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Services.DoctorateServiceImpl;
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

import java.io.IOException;

@Controller
@RestController
@RequestMapping("/docteur")
@CrossOrigin(origins = "http://localhost:4200")
public class DocteurController {

    private final AuthenticationManager authenticationManager;
    private final DoctorateServiceImpl docteurService;;
    private final JwtUtil jwtUtil;


    @Autowired
    public DocteurController(AuthenticationManager authenticationManager, DoctorateServiceImpl docteurService, JwtUtil jwtUtil1) {
        this.authenticationManager = authenticationManager;
        this.docteurService = docteurService;

        this.jwtUtil = jwtUtil1;
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
        // Corriger l'email avant de l'utiliser
        String originalEmail = loginRequest.getEmail();
        if (originalEmail.contains("00") && !originalEmail.contains("@")) {
            String correctedEmail = originalEmail.replace("00", "@");
            loginRequest.setEmail(correctedEmail);
            System.out.println("Email corrigé dans le contrôleur: " + correctedEmail);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect email or password.");
        } catch (DisabledException disabledException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "docteur is not activated");
            return null;
        }

        final UserDetails userDetails = docteurService.loadUserByUsername(loginRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return new LoginResponse(jwt);
    }


}
