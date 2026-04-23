package medico.PPE.Services;


import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ZoomAuthService {

    private final ZoomTokenService tokenService;
    private final TokenStore tokenStore;
    private final String clientId;
    private final String clientSecret;

    public ZoomAuthService(
            ZoomTokenService tokenService,
            TokenStore tokenStore,
            @Value("${zoom.client-id}") String clientId,
            @Value("${zoom.client-secret}") String clientSecret) {
        this.tokenService = tokenService;
        this.tokenStore = tokenStore;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    /**
     * Retourne un token valide, ou null si aucun token n'est disponible.
     * Ne lève JAMAIS d'exception — les appelants gèrent le cas null.
     */
    // ZoomAuthService.java — getValidAccessToken()
    public String getValidAccessToken() {
        try {
            String accessToken = tokenStore.getAccessToken(); // ← retourne null si absent
    
            if (accessToken == null || accessToken.isBlank()) {
                String refreshToken = tokenStore.getRefreshToken();
                if (refreshToken == null || refreshToken.isBlank()) {
                    return null; // pas encore connecté, c'est normal
                }
                refreshNow();
                return tokenStore.getAccessToken();
            }
    
            if (tokenStore.isTokenExpired()) { // ← utilise la méthode corrigée
                String refreshToken = tokenStore.getRefreshToken();
                if (refreshToken == null || refreshToken.isBlank()) {
                    return null;
                }
                refreshNow();
                return tokenStore.getAccessToken();
            }
    
            return accessToken;
    
        } catch (Exception e) {
            System.err.println("Erreur token Zoom : " + e.getMessage());
            return null;
        }
    }    public ZoomTokenService.ZoomToken refreshNow() throws Exception {
        String refreshToken = tokenStore.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token non disponible. Veuillez autoriser Zoom à nouveau.");
        }

        ZoomTokenService.ZoomToken refreshed = tokenService.refreshAccessToken(
                refreshToken, clientId, clientSecret);

        tokenStore.saveToken(
                refreshed.accessToken(),
                refreshed.refreshToken(),
                refreshed.expiresInSeconds());

        return refreshed;
    }

 
}