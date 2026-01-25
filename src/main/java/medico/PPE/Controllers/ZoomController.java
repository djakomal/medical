package medico.PPE.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import medico.PPE.Services.TokenStore;
import medico.PPE.Services.ZoomMeetingService;
import medico.PPE.Services.ZoomTokenService;
import medico.PPE.dtos.RegistrantResponse;
import medico.PPE.dtos.ZoomRegistrantDto;
import medico.PPE.dtos.ZoomResponse;
import medico.PPE.dtos.ZoomUpdateDto;
import medico.PPE.dtos.zoomDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.awt.print.Pageable;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
//@RequiredArgsConstructor
public class ZoomController {

    public ZoomController(ZoomMeetingService meetingService,
                          ZoomTokenService tokenService,
                          TokenStore tokenStore) {
        this.meetingService = meetingService;
        this.tokenService = tokenService;
        this.tokenStore = tokenStore;
    }


    private final ZoomMeetingService meetingService;
    private final ZoomTokenService tokenService;
    private final TokenStore tokenStore;

    @Value("${zoom.client-id}")
    private String clientId;

    @Value("${zoom.client-secret}")
    private String clientSecret;

//    @Value("${zoom.account-id}")
//    private String accountId;

    @Value("${zoom.redirect-uri}")
    private String redirectUri;


    @GetMapping("/authorize")
    public void redirectToZoomAuth(HttpServletResponse response) throws IOException {
        String authUrl = String.format(
                "https://zoom.us/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s",
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
        );
        response.sendRedirect(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleZoomRedirect(@RequestParam("code") String code) {
        try {
            System.out.println("Authorization code: " + code);
            System.out.println("Redirect URI: " + redirectUri);
            JsonNode tokenData = tokenService.exchangeAuthCodeForAccessToken(code, redirectUri, clientId, clientSecret);
            String accessToken = tokenData.get("access_token").asText();
            String refreshToken = tokenData.get("refresh_token").asText();
            int expiresIn = tokenData.get("expires_in").asInt();

            tokenStore.saveToken(accessToken, refreshToken, expiresIn);

            System.out.println("Stored access token: " + tokenStore.getAccessToken());
            System.out.println("Stored refresh token: " + tokenStore.getRefreshToken());


            return ResponseEntity.ok("Zoom token successfully created.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken() {
        System.out.println("Using refresh token: " + tokenStore.getRefreshToken());
        try {
            String newAccessToken = tokenService.refreshAccessToken(
                    tokenStore.getRefreshToken(),
                    clientId,
                    clientSecret
            );

            tokenStore.saveToken(newAccessToken, tokenStore.getRefreshToken(), 3600);

            System.out.println(newAccessToken);

            return ResponseEntity.ok("Access token refreshed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to refresh token: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity <?> createMeeting(@RequestBody zoomDto request) throws Exception {
        try {
            String token = tokenStore.getAccessToken();

            if (tokenStore.isExpired()) {
                token = tokenService.refreshAccessToken(
                        tokenStore.getRefreshToken(),
                        clientId,
                        clientSecret
                );
                tokenStore.saveToken(token, tokenStore.getRefreshToken(), 3600);
            }
//        String token = tokenService.getAccessToken(clientId, clientSecret, accountId);

            ZoomResponse response =  meetingService.createMeeting(
                    token,
                    request.getTopic(),
                    request.getStartTime(),
                    request.getDuration(),
                    request.getTimezone()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create meeting: " + e.getMessage());
            }
    }

    @GetMapping("/me")
    public ResponseEntity<String> getAuthenticatedZoomUser() {
        try {

            String accessToken = tokenStore.getAccessToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.zoom.us/v2/users/me"))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if token is invalid or expired
            if (response.statusCode() == 401) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired access token.");
            }

            return ResponseEntity.ok(response.body());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<JsonNode> getAllMeetings(Pageable pageable) {
        try {
            String accessToken = tokenStore.getAccessToken();
            JsonNode meetings = meetingService.getAllMeetings(accessToken);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ObjectMapper().createObjectNode().put("error", e.getMessage()));
        }
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<JsonNode> getMeetingById(@PathVariable String meetingId) {
        try {
            String accessToken = tokenStore.getAccessToken();
            JsonNode meeting = meetingService.getMeetingById(accessToken, meetingId);
            return ResponseEntity.ok(meeting);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ObjectMapper().createObjectNode().put("error", e.getMessage()));
        }
    }

    @PatchMapping("/{meetingId}")
    public ResponseEntity<String> updateMeeting(@PathVariable String meetingId, @RequestBody ZoomUpdateDto updateDto) {
        try {
            String token = tokenStore.getAccessToken();

            if (tokenStore.isExpired()) {
                token = tokenService.refreshAccessToken(
                        tokenStore.getRefreshToken(),
                        clientId,
                        clientSecret
                );
                tokenStore.saveToken(token, tokenStore.getRefreshToken(), 3600);
            }

            meetingService.updateMeeting(token, meetingId, updateDto);
            return ResponseEntity.ok("Meeting updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable String meetingId) {
        try {
            String accessToken = tokenStore.getAccessToken();

            if (tokenStore.isExpired()) {
                accessToken = tokenService.refreshAccessToken(
                        tokenStore.getRefreshToken(),
                        clientId,
                        clientSecret
                );
                tokenStore.saveToken(accessToken, tokenStore.getRefreshToken(), 3600);
            }

            meetingService.deleteMeeting(accessToken, meetingId);
            return ResponseEntity.ok("Meeting deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete meeting: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerToMeeting(@RequestBody ZoomRegistrantDto dto) {
        try {
            String token = tokenStore.getAccessToken();
            if (tokenStore.isExpired()) {
                token = tokenService.refreshAccessToken(
                        tokenStore.getRefreshToken(),
                        clientId,
                        clientSecret
                );
                tokenStore.saveToken(token, tokenStore.getRefreshToken(), 3600);
            }

            RegistrantResponse response = meetingService.registerAttendee(token, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }

}
