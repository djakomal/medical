package medico.PPE.Services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Service
public class ZoomTokenService {

    public record ZoomToken(String accessToken, String refreshToken, int expiresInSeconds) {}

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public ZoomToken exchangeAuthCodeForAccessToken(
            String code,
            String redirectUri,
            String clientId,
            String clientSecret) throws Exception {

        String auth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        String url = "https://zoom.us/oauth/token" +
                "?grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + auth)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Zoom OAuth error: " + response.body());
        }

        JsonNode json = mapper.readTree(response.body());

        return new ZoomToken(
                json.get("access_token").asText(),
                json.get("refresh_token").asText(),
                json.get("expires_in").asInt()
        );
    }

    public ZoomToken refreshAccessToken(String refreshToken, String clientId, String clientSecret) throws Exception {

        String auth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        String url = "https://zoom.us/oauth/token" +
                "?grant_type=refresh_token" +
                "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + auth)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Refresh token failed: " + response.body());
        }

        JsonNode json = mapper.readTree(response.body());

        return new ZoomToken(
                json.get("access_token").asText(),
                json.get("refresh_token").asText(),
                json.get("expires_in").asInt()
        );
    }
}