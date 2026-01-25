package medico.PPE.Services;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenStore {
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;

    public synchronized void saveToken(String accessToken, String refreshToken, int expiresInSeconds) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = Instant.now().plusSeconds(expiresInSeconds - 60);
    }

    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            throw new RuntimeException("Access token expired or not available.");
        }
        return accessToken;
    }

    public synchronized String getRefreshToken() {
        return refreshToken;
    }

    public synchronized boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
