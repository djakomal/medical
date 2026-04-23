package medico.PPE.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import medico.PPE.Services.TokenStore;
import medico.PPE.Services.ZoomAuthService;
import medico.PPE.Services.ZoomMeetingService;
import medico.PPE.Services.ZoomTokenService;
import medico.PPE.Services.ZoomTokenService.ZoomToken;
import medico.PPE.dtos.ZoomRegistrantDto;
import medico.PPE.dtos.ZoomResponse;
import medico.PPE.dtos.ZoomUpdateDto;
import medico.PPE.dtos.zoomDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class ZoomController {

    private final ZoomMeetingService meetingService;
    private final ZoomTokenService tokenService;
    private final TokenStore tokenStore;
    private final ZoomAuthService zoomAuthService;

    @Value("${zoom.client-id}")
    private String clientId;

    @Value("${zoom.client-secret}")
    private String clientSecret;

    @Value("${zoom.redirect-uri}")
    private String redirectUri;

    public ZoomController(ZoomMeetingService meetingService,
                          ZoomTokenService tokenService,
                          TokenStore tokenStore,
                          ZoomAuthService zoomAuthService) {
        this.meetingService = meetingService;
        this.tokenService = tokenService;
        this.tokenStore = tokenStore;
        this.zoomAuthService = zoomAuthService;
    }

    // ==================== AUTH ====================

    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> getZoomAuthUrl() {
        try {
            String scopes = URLEncoder.encode(
                "meeting:write:meeting meeting:read:meeting meeting:delete:meeting meeting:update:meeting",
                StandardCharsets.UTF_8
            );

            String authUrl = String.format(
                "https://zoom.us/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s",
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                scopes
            );

            Map<String, String> response = new HashMap<>();
            response.put("authUrl", authUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate auth URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleZoomRedirect(
            @RequestParam(value = "code", required = false) String code) {

        if (code == null) {
            String errorHtml = """
                <html><body><script>
                    window.opener?.postMessage({type:'zoom_auth_error', message:'Code manquant'}, '*');
                    window.close();
                </script></body></html>
                """;
            return ResponseEntity.badRequest()
                    .header("Content-Type", "text/html")
                    .body(errorHtml);
        }

        try {
            ZoomToken tokenData = tokenService.exchangeAuthCodeForAccessToken(
                    code, redirectUri, clientId, clientSecret);

            tokenStore.saveToken(
                    tokenData.accessToken(),
                    tokenData.refreshToken(),
                    tokenData.expiresInSeconds()
            );

            String successHtml = """
                <html>
                <head><title>Zoom Auth</title></head>
                <body>
                    <p>✅ Connexion Zoom réussie ! Fermeture en cours...</p>
                    <script>
                        try {
                            window.opener.postMessage({ type: 'zoom_auth_success' }, '*');
                        } catch(e) {}
                        setTimeout(() => window.close(), 1000);
                    </script>
                </body>
                </html>
                """;

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html")
                    .body(successHtml);

        } catch (Exception e) {
            String errorHtml = String.format("""
                <html><body><script>
                    window.opener?.postMessage({type:'zoom_auth_error', message:'%s'}, '*');
                    window.close();
                </script></body></html>
                """, e.getMessage().replace("'", "\\'"));

            return ResponseEntity.status(500)
                    .header("Content-Type", "text/html")
                    .body(errorHtml);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthStatus() {
        // ✅ getValidAccessToken() retourne null sans exception
        String accessToken = zoomAuthService.getValidAccessToken();
    
        if (accessToken == null || accessToken.isBlank()) {
            // 200 avec authenticated:false — pas une erreur, juste non connecté
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
    
        return ResponseEntity.ok(Map.of("authenticated", true));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken() {
        try {
            zoomAuthService.refreshNow();
            return ResponseEntity.ok("Token rafraîchi avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Échec du rafraîchissement : " + e.getMessage());
        }
    }

    // ==================== MEETINGS ====================
    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestBody zoomDto request) {
        try {
            String token = zoomAuthService.getValidAccessToken();
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Zoom non connecté. Authentifiez-vous d'abord.");
            }
    
            // ✅ Utilise la surcharge qui respecte le type (1=instant, 2=scheduled)
            ZoomResponse response = meetingService.createMeeting(token, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Échec création: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<JsonNode> getAllMeetings(Pageable pageable) {
        try {
            String accessToken = zoomAuthService.getValidAccessToken();
            JsonNode meetings = meetingService.getAllMeetings(accessToken);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ObjectMapper().createObjectNode().put("error", e.getMessage()));
        }
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<JsonNode> getMeetingById(@PathVariable String meetingId) {
        try {
            String accessToken = zoomAuthService.getValidAccessToken();
            JsonNode meeting = meetingService.getMeetingById(accessToken, meetingId);
            return ResponseEntity.ok(meeting);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ObjectMapper().createObjectNode().put("error", e.getMessage()));
        }
    }

    @PatchMapping("/{meetingId}")
    public ResponseEntity<String> updateMeeting(
            @PathVariable String meetingId,
            @RequestBody ZoomUpdateDto updateDto) {
        try {
            String token = zoomAuthService.getValidAccessToken();
            meetingService.updateMeeting(token, meetingId, updateDto);
            return ResponseEntity.ok("Réunion mise à jour avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable String meetingId) {
        try {
            String accessToken = zoomAuthService.getValidAccessToken();
            meetingService.deleteMeeting(accessToken, meetingId);
            return ResponseEntity.ok("Réunion supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Échec de la suppression : " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerToMeeting(@RequestBody ZoomRegistrantDto dto) {
        try {
            String token = zoomAuthService.getValidAccessToken();
            Object response = meetingService.registerAttendee(token, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Inscription échouée : " + e.getMessage());
        }
    }
}