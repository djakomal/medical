
package medico.PPE.Services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomService {
    
    @Value("${zoom.client-id}")
    private String clientId;
    
    @Value("${zoom.client-secret}")
    private String clientSecret;
    
    @Value("${zoom.redirect-uri}")
    private String redirectUri;
    
    @Value("${zoom.token-uri}")
    private String tokenUri;
    
    @Value("${zoom.api-base-url}")
    private String apiBaseUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ZoomService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    // Générer l'URL d'autorisation
    public String getAuthorizationUrl() {
        return String.format(
            "https://zoom.us/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s",
            clientId, redirectUri
        );
    }
    
    // Échanger le code contre un access token
    public Map<String, Object> getAccessToken(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        // Encodage Base64 pour l'authentification Basic
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", jsonNode.get("access_token").asText());
        result.put("token_type", jsonNode.get("token_type").asText());
        result.put("expires_in", jsonNode.get("expires_in").asInt());
        result.put("scope", jsonNode.get("scope").asText());
        
        if (jsonNode.has("refresh_token")) {
            result.put("refresh_token", jsonNode.get("refresh_token").asText());
        }
        
        return result;
    }
    
    // Rafraîchir le token
    public Map<String, Object> refreshAccessToken(String refreshToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", jsonNode.get("access_token").asText());
        result.put("token_type", jsonNode.get("token_type").asText());
        result.put("expires_in", jsonNode.get("expires_in").asInt());
        result.put("scope", jsonNode.get("scope").asText());
        
        return result;
    }
    
    // Obtenir les informations de l'utilisateur
    public JsonNode getUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            apiBaseUrl + "/users/me",
            HttpMethod.GET,
            entity,
            String.class
        );
        
        return objectMapper.readTree(response.getBody());
    }
    
    // Créer une réunion
    public JsonNode createMeeting(String accessToken, Map<String, Object> meetingData) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String jsonBody = objectMapper.writeValueAsString(meetingData);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            apiBaseUrl + "/users/me/meetings",
            HttpMethod.POST,
            entity,
            String.class
        );
        
        return objectMapper.readTree(response.getBody());
    }
    
    // Lister les réunions
    public JsonNode listMeetings(String accessToken, String type) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = apiBaseUrl + "/users/me/meetings";
        if (type != null && !type.isEmpty()) {
            url += "?type=" + type;
        }
        
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            String.class
        );
        
        return objectMapper.readTree(response.getBody());
    }
    
    // Obtenir les détails d'une réunion
    public JsonNode getMeeting(String accessToken, String meetingId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            apiBaseUrl + "/meetings/" + meetingId,
            HttpMethod.GET,
            entity,
            String.class
        );
        
        return objectMapper.readTree(response.getBody());
    }
    
    // Supprimer une réunion
    public void deleteMeeting(String accessToken, String meetingId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        restTemplate.exchange(
            apiBaseUrl + "/meetings/" + meetingId,
            HttpMethod.DELETE,
            entity,
            String.class
        );
    }
}
