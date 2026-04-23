package medico.PPE.Services;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenStore {

    @Autowired
    private medico.PPE.Repositories.ZoomTokenRepository zoomTokenRepository;

    public synchronized void saveToken(String accessToken, String refreshToken, int expiresInSeconds) {
        long safeExpiresInSeconds = Math.max(0L, (long) expiresInSeconds - 60L);
        Instant expiresAt = Instant.now().plusSeconds(safeExpiresInSeconds);

        zoomTokenRepository.deleteAll();

        medico.PPE.Models.ZoomToken token = new medico.PPE.Models.ZoomToken(
                accessToken, refreshToken, expiresAt);
        zoomTokenRepository.save(token);
    }

    /**
     * Retourne le token ou NULL si absent — ne lève jamais d'exception.
     */
    public synchronized String getAccessToken() {
        Optional<medico.PPE.Models.ZoomToken> tokenOpt =
                zoomTokenRepository.findTopByOrderByIdDesc();

        if (tokenOpt.isEmpty() || tokenOpt.get().getAccessToken() == null) {
            return null; // ✅ null au lieu d'une exception
        }
        return tokenOpt.get().getAccessToken();
    }

    public synchronized String getRefreshToken() {
        Optional<medico.PPE.Models.ZoomToken> tokenOpt =
                zoomTokenRepository.findTopByOrderByIdDesc();
        return tokenOpt.map(medico.PPE.Models.ZoomToken::getRefreshToken).orElse(null);
    }

    /**
     * Vérifie si le token est expiré — retourne true si aucun token en base.
     */
    public synchronized boolean isTokenExpired() {
        Optional<medico.PPE.Models.ZoomToken> tokenOpt =
                zoomTokenRepository.findTopByOrderByIdDesc();

        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiresAt() == null) {
            return true; // ✅ pas de token = considéré comme expiré
        }
        return Instant.now().isAfter(tokenOpt.get().getExpiresAt());
    }

    /**
     * @deprecated Utiliser isTokenExpired() à la place
     */
    @Deprecated
    public boolean isExpired() {
        return isTokenExpired();
    }
}