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
import java.util.Set;

@Component
@Order(1)
public class CustomerJwtRequestFilter extends BaseJwtRequestFilter {
    
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "/login/login",
        "/signup/client",
        "/signup/docteur"
    );

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public CustomerJwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        super(jwtUtil);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // ✅ IMPORTANT : Skip JWT validation pour les endpoints publics
        String path = request.getRequestURI();
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::endsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Appeler la méthode parent
        super.doFilterInternal(request, response, filterChain);
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
                
                // ✅ CONTINUER UNIQUEMENT SI SUCCÈS
                filterChain.doFilter(request, response);
            } else {
                // ❌ Token invalide → BLOQUER ET NE PAS CONTINUER
                logger.warn("⚠️ Token invalide pour: " + username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token JWT invalide ou expiré\"}");
                // NE PAS APPELER filterChain.doFilter() ICI !
            }
            
        } catch (UsernameNotFoundException e) {
            // ❌ Utilisateur non trouvé → BLOQUER ET NE PAS CONTINUER
            logger.warn("⚠️ Utilisateur non trouvé: " + username);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Utilisateur non trouvé\"}");
            // NE PAS APPELER filterChain.doFilter() ICI !
            
        } catch (Exception e) {
            // ❌ Erreur inattendue → BLOQUER ET NE PAS CONTINUER
            logger.error("🔥 Erreur lors de l'authentification: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Erreur d'authentification\"}");
            // NE PAS APPELER filterChain.doFilter() ICI !
        }
    }
}