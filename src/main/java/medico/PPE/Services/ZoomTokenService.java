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
//    public String getAccessToken(String clientId, String clientSecret, String accountId) throws IOException, InterruptedException {
//        String tokenUrl = "https://zoom.us/oauth/token";
//        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(tokenUrl + "?grant_type=account_credentials&account_id=" + accountId))
//                .header("Authorization", "Basic " + auth)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .POST(HttpRequest.BodyPublishers.noBody())
//                .build();
//
//        System.out.println("Sending request to Zoom...");
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode json = mapper.readTree(response.body());
//
//        if (!json.has("access_token")) {
//            throw new RuntimeException("Zoom did not return an access token: " + json);
//        }
//        System.out.println("Token: " + json.get("access_token").asText());
//        return json.get("access_token").asText();
//    }
public JsonNode exchangeAuthCodeForAccessToken(String code, String redirectUri, String clientId, String clientSecret) throws Exception {
    String tokenUrl = "https://zoom.us/oauth/token";

    String auth = Base64.getEncoder()
            .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tokenUrl + "?grant_type=authorization_code&code=" + code + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8")))
            .header("Authorization", "Basic " + auth)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(response.body());
    System.out.println(json);

    if (!json.has("access_token")) {
        throw new RuntimeException("Failed to get access token: " + json);
    }

    // ⬇️ Store these in your DB
    String accessToken = json.get("access_token").asText();
    String refreshToken = json.get("refresh_token").asText();


//    return accessToken;
    return json;
}

    public String refreshAccessToken(String refreshToken, String clientId, String clientSecret) throws Exception {
        String tokenUrl = "https://zoom.us/oauth/token";

        String auth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl + "?grant_type=refresh_token&refresh_token=" + refreshToken))
                .header("Authorization", "Basic " + auth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        if (!json.has("access_token")) {
            throw new RuntimeException("Failed to refresh access token: " + json);
        }

        return json.get("access_token").asText();
    }

}