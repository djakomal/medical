package medico.PPE.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.Services.DocteurService;
import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.jwt.CustomerServiceImpl;
import medico.PPE.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomerServiceImpl customerService;

    private final DoctorateServiceImpl docteurServiceImpl;


    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(CustomerServiceImpl customerService, DoctorateServiceImpl docteurServiceImpl, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.docteurServiceImpl = docteurServiceImpl;



        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Vérifier si le token est présent dans l'en-tête Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("JWT extraction failed: " + e.getMessage());
            }
        }

        // Si le username est présent et qu'aucune authentification n'est encore établie
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
           UserDetails userDetails = customerService.loadUserByUsername(username);
            UserDetails docteurDetails = docteurServiceImpl.loadUserByUsername(username);

            // : chercher d'abord parmi les customers


            // Vérifier si le token est valide
            if (userDetails != null && jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Ajouter l'authentification au contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
              if(docteurDetails != null && jwtUtil.validateToken(token, docteurDetails)){
                 UsernamePasswordAuthenticationToken authenticationToken =
                         new UsernamePasswordAuthenticationToken(docteurDetails, null, docteurDetails.getAuthorities());
                 authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                 // Ajouter l'authentification au contexte de sécurité
                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
             }
            else {
                // Si le token est invalide ou expiré, envoyer une erreur 401
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
