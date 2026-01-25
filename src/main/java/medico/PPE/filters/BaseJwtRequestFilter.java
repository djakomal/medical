package medico.PPE.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import medico.PPE.utils.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public abstract class BaseJwtRequestFilter extends OncePerRequestFilter {

    protected final JwtUtil jwtUtil;

    public BaseJwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
                
                // Vérifier si le token doit être rafraîchi automatiquement
                if (jwtUtil.shouldRefreshToken(token)) {
                    String newToken = jwtUtil.refreshToken(token);
                    // Envoyer le nouveau token dans le header de réponse
                    response.setHeader("X-New-Token", newToken);
                    logger.info("Token automatiquement rafraîchi pour l'utilisateur: " + username);
                }
                
            } catch (Exception e) {
                logger.error("JWT extraction failed: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            processAuthentication(request, response, filterChain, username, token);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    protected abstract void processAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                   FilterChain filterChain, String username, String token)
            throws ServletException, IOException;
}