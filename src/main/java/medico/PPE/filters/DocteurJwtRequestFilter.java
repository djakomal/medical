package medico.PPE.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(2) // Ordre de priorité (s'exécute en second)
public class DocteurJwtRequestFilter extends BaseJwtRequestFilter  {

    private final DoctorateServiceImpl docteurService;

    @Autowired
    public DocteurJwtRequestFilter(DoctorateServiceImpl docteurService, JwtUtil jwtUtil) {
        super(jwtUtil);
        this.docteurService = docteurService;
    }

    @Override
    protected void processAuthentication(HttpServletRequest request, HttpServletResponse response,
                                         FilterChain filterChain, String username, String token)
            throws ServletException, IOException {
        // Ne traiter que si l'authentification n'est pas déjà établie
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = docteurService.loadUserByUsername(username);

                // Vérifier si le token est valide
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Ajouter l'authentification au contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("Authenticated as docteur: " + username);
                }
            } catch (UsernameNotFoundException e) {
                logger.debug("User not found in doctors: " + username);
            }
        }

        // Si aucune authentification n'a réussi après les deux filtres
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        // Continuer avec le reste de la chaîne
        filterChain.doFilter(request, response);
    }
}