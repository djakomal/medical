package medico.PPE.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.jwt.CustomerServiceImpl;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1) // Ordre de priorité (s'exécute en premier)
public class CustomerJwtRequestFilter extends BaseJwtRequestFilter {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerJwtRequestFilter(CustomerServiceImpl customerService, JwtUtil jwtUtil) {
        super(jwtUtil);
        this.customerService = customerService;
    }

    @Override
    protected void processAuthentication(HttpServletRequest request, HttpServletResponse response,
                                         FilterChain filterChain, String username, String token)
            throws ServletException, IOException {
        try {
            UserDetails userDetails = customerService.loadUserByUsername(username);

            // Vérifier si le token est valide
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Ajouter l'authentification au contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("Authenticated as customer: " + username);
            }
        } catch (UsernameNotFoundException e) {
            // Utilisateur non trouvé dans la table customer, on passe au filtre suivant
            logger.debug("User not found in customers: " + username);
        }

        // Continuer avec le filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }
}