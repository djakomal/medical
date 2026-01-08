package medico.PPE.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.Services.UserDetailsServiceImpl;
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
@Order(1)
public class CustomerJwtRequestFilter extends BaseJwtRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public CustomerJwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        super(jwtUtil);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void processAuthentication(HttpServletRequest request, HttpServletResponse response,
                                         FilterChain filterChain, String username, String token)
            throws ServletException, IOException {
        try {
            // 1️⃣ Charger l'utilisateur (Customer OU Docteur)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.info("🔍 Utilisateur trouvé: " + username + " avec rôles: " + userDetails.getAuthorities());

            // 2️⃣ Valider le token
            if (jwtUtil.validateToken(token, userDetails)) {
                // ✅ Token valide → authentifier
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                logger.info("✅ Authentification réussie pour: " + username);
            } else {
                // ❌ Token invalide → BLOQUER
                logger.warn("❌ Token invalide pour: " + username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token JWT invalide ou expiré\"}");
                return;
            }
            
        } catch (UsernameNotFoundException e) {
            // Utilisateur non trouvé
            logger.warn("❌ Utilisateur non trouvé: " + username);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Utilisateur non trouvé\"}");
            return;
            
        } catch (Exception e) {
            // Erreur inattendue
            logger.error("🔥 Erreur lors de l'authentification: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Erreur d'authentification\"}");
            return;
        }

        // Continuer avec le filtre suivant
        filterChain.doFilter(request, response);
    }
}