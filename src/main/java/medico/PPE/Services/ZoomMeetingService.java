package medico.PPE.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import medico.PPE.Controllers.RegistrantResponse;
import medico.PPE.Models.ZoomMapper;
import medico.PPE.Models.ZoomMeeting;
import medico.PPE.Repositories.ZoomMeetingRepository;
import medico.PPE.dtos.ZoomRegistrantDto;
import medico.PPE.dtos.ZoomResponse;
import medico.PPE.dtos.ZoomUpdateDto;
import medico.PPE.dtos.zoomDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class ZoomMeetingService {

    private final ZoomMeetingRepository zoomMeetingRepository;
    private final ZoomMapper zoomMapper;

    public ZoomMeetingService(ZoomMeetingRepository zoomMeetingRepository, ZoomMapper zoomMapper) {
        this.zoomMeetingRepository = zoomMeetingRepository;
        this.zoomMapper = zoomMapper;
    }

    // ==================== CRÉER UNE RÉUNION ====================

    /**
     * Création simple (appelée par AppServiceImp pour les RDV)
     */
    public ZoomResponse createMeeting(String accessToken, String topic, String startTime,
        int durationInMinutes, String timezone)
         throws Exception {
    return createMeeting(accessToken, topic, startTime, durationInMinutes, timezone, 2, true);
                            
         }



    /**
     * Création complète avec type et settings (appelée par ZoomController)
     */
    public ZoomResponse createMeeting(String accessToken, zoomDto request) throws Exception {
        int type = request.getType() > 0 ? request.getType() : 2;

        // Pour les réunions instantanées (type=1), startTime et timezone ne sont pas requis
        String startTime = request.getStartTime();
        String timezone  = request.getTimezone() != null ? request.getTimezone() : "Africa/Lome";

        boolean waitingRoom = false;
        boolean meetingAuth = false;
        if (request.getSettings() != null) {
            waitingRoom = request.getSettings().isWaitingRoom();
            meetingAuth = request.getSettings().isMeetingAuthentication();
        }

        return createMeeting(accessToken, request.getTopic(), startTime,
                request.getDuration(), timezone, type, waitingRoom);
    }

    /**
     * Méthode interne commune
     */
    private ZoomResponse createMeeting(String accessToken, String topic, String startTime,
                                        int durationInMinutes, String timezone,
                                        int type, boolean waitingRoom) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();

        body.put("topic", topic != null ? topic : "Consultation médicale");
        body.put("type", type);
        body.put("duration", durationInMinutes > 0 ? durationInMinutes : 30);

        // startTime et timezone seulement pour les réunions planifiées (type != 1)
        if (type != 1 && startTime != null && !startTime.isBlank()) {
            body.put("start_time", startTime);
            body.put("timezone", timezone);
        }
        // Dans la méthode interne createMeeting(), après les settings existants :
        ObjectNode settings = mapper.createObjectNode();
        settings.put("host_video", true);
        settings.put("participant_video", true);
        settings.put("waiting_room", waitingRoom);
        settings.put("meeting_authentication", false);
        settings.put("join_before_host", false);
        //  Ajouter ces deux lignes :
        settings.put("approval_type", 2);           // pas d'inscription requise mais waiting room actif
        settings.put("registrants_email_notification", false);
        
        body.set("settings", settings);

        System.out.println(" Zoom request payload:\n" + body.toPrettyString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/users/me/meetings"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(" Zoom response status: " + response.statusCode());
        System.out.println(" Zoom response body: " + response.body());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Zoom API error " + response.statusCode() + ": " + response.body());
        }

        JsonNode json = mapper.readTree(response.body());

        if (!json.hasNonNull("id") || !json.hasNonNull("join_url")) {
            throw new RuntimeException("Réponse Zoom inattendue: " + json);
        }

        ZoomResponse zoomResponse = new ZoomResponse(
                json.get("id").asText(),
                json.path("topic").asText(),
                json.path("start_time").asText(""),
                json.get("join_url").asText(),
                json.path("start_url").asText("")
        );

        // Sauvegarder en base
        ZoomMeeting meeting = zoomMapper.mapToEntity(zoomResponse);
        zoomMeetingRepository.save(meeting);

        return zoomResponse;
    }

    // ==================== LISTER LES RÉUNIONS ====================

    public JsonNode getAllMeetings(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/users/me/meetings"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Zoom API error " + response.statusCode() + ": " + response.body());
        }

        return new ObjectMapper().readTree(response.body());
    }

    // ==================== DÉTAIL D'UNE RÉUNION ====================

    public JsonNode getMeetingById(String accessToken, String meetingId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/meetings/" + meetingId))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Zoom API error " + response.statusCode() + ": " + response.body());
        }

        return new ObjectMapper().readTree(response.body());
    }

    // ==================== MODIFIER UNE RÉUNION ====================

    public void updateMeeting(String accessToken, String meetingId, ZoomUpdateDto updateDto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();

        if (updateDto.getTopic() != null)    body.put("topic", updateDto.getTopic());
        if (updateDto.getStartTime() != null) body.put("start_time", updateDto.getStartTime());
        if (updateDto.getDuration() > 0)     body.put("duration", updateDto.getDuration());
        if (updateDto.getTimezone() != null) body.put("timezone", updateDto.getTimezone());

        ObjectNode settings = mapper.createObjectNode();
        settings.put("host_video", updateDto.isHostVideo());
        settings.put("participant_video", updateDto.isParticipantVideo());
        body.set("settings", settings);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/meetings/" + meetingId))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Zoom update error " + response.statusCode() + ": " + response.body());
        }
    }

    // ==================== SUPPRIMER UNE RÉUNION ====================

    public void deleteMeeting(String accessToken, String meetingId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/meetings/" + meetingId))
                .header("Authorization", "Bearer " + accessToken)
                .DELETE()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Zoom delete error " + response.statusCode() + ": " + response.body());
        }

        System.out.println("✅ Meeting supprimé: " + meetingId);
    }

    // ==================== INSCRIRE UN PARTICIPANT ====================

    public RegistrantResponse registerAttendee(String accessToken, ZoomRegistrantDto dto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("email", dto.getEmail());
        body.put("first_name", dto.getFirstName());
        body.put("last_name", dto.getLastName());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/meetings/" + dto.getMeetingId() + "/registrants"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode json = mapper.readTree(response.body());

        if (response.statusCode() >= 400 || json.has("code")) {
            throw new RuntimeException("Inscription échouée: " + json.toPrettyString());
        }

        return new RegistrantResponse(
                json.path("join_url").asText(""),
                json.path("registrant_id").asText(""),
                json.path("start_time").asText("")
        );
    }
}