package medico.PPE.Services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import medico.PPE.Models.ZoomMapper;
import medico.PPE.Models.ZoomMeeting;
import medico.PPE.Repositories.ZoomMeetingRepository;
import medico.PPE.dtos.OrganizerDto;
import medico.PPE.dtos.RegistrantResponse;
import medico.PPE.dtos.SpeakersDto;
import medico.PPE.dtos.ZoomRegistrantDto;
import medico.PPE.dtos.ZoomResponse;
import medico.PPE.dtos.ZoomUpdateDto;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZoomMeetingService {

    private final ZoomMeetingRepository zoomMeetingRepository;
    private final ZoomMapper zoomMapper;

    public ZoomMeetingService(ZoomMeetingRepository zoomMeetingRepository, ZoomMapper zoomMapper) {
        this.zoomMeetingRepository = zoomMeetingRepository;
        this.zoomMapper = zoomMapper;
    }

    public ZoomResponse createMeeting(String accessToken, String topic, String startTime, int durationInMinutes, String timezone)
            throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("topic", topic);
        body.put("type", 2);
        body.put("start_time", startTime);
        body.put("duration", durationInMinutes);
//        body.put("attendees", attendees);
        body.put("timezone", timezone);

        System.out.println("Request payload: " + body.toPrettyString());

        ObjectNode settings = mapper.createObjectNode();
        settings.put("host_video", true);
        settings.put("participant_video", true);
        settings.put("waiting_room", true);
        settings.put("approval_type", 0);
        settings.put("registration_type", 1);
        settings.put("meeting_authentication", true);
        settings.put("join_before_host", false);
        settings.put("registrants_email_notification", true);

        body.set("settings", settings);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/users/me/meetings"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Zoom response status: " + response.statusCode());
        System.out.println("Zoom response body: " + response.body());
        JsonNode json = mapper.readTree(response.body());

        List<SpeakersDto> speakers = new ArrayList<>();
        speakers.add(new SpeakersDto("John Doe", "john@example.com", "Keynote Speaker"));
        speakers.add(new SpeakersDto("Jane Smith", "jane@example.com", "Panelist"));
//        JsonNode organizersNode = json.get("organizerName");
        List<OrganizerDto> organizers = new ArrayList<>();
        organizers.add(new OrganizerDto("Veronica", "veronica@example.com"));

        ZoomResponse zoomResponse = new ZoomResponse(
                json.get("id").asText(),
                json.get("topic").asText(),
                json.get("start_time").asText(),
                json.get("join_url").asText(),
                json.get("start_url").asText()
        );

        ZoomMeeting meeting = zoomMapper.mapToEntity(zoomResponse);
        zoomMeetingRepository.save(meeting);

//        Map<String, String> result = new HashMap<>();
//
//        result.put("join_url", json.get("join_url").asText());
//        result.put("start_url", json.get("start_url").asText());
//        result.put("id", json.get("id").asText());
//
//
//        System.out.println("Result: " + result);
//        return result;
        return zoomResponse;
    }


    // Get all meetings
    public JsonNode getAllMeetings(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/users/me/meetings"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body());
    }

    // Get a specific meeting by ID
    public JsonNode getMeetingById(String accessToken, String meetingId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/meetings/" + meetingId))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.body());
    }

    public void updateMeeting(String accessToken, String meetingId, ZoomUpdateDto updateDto) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("topic", updateDto.getTopic());
        body.put("start_time", updateDto.getStartTime());
        body.put("duration", updateDto.getDuration());
        body.put("timezone", updateDto.getTimezone());

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

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Failed to update meeting: " + response.body());
        }
    }

    public RegistrantResponse registerAttendee(String accessToken, ZoomRegistrantDto dto) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode body = mapper.createObjectNode();
        body.put("email", dto.getEmail());
        body.put("first_name", dto.getFirstName());
        body.put("last_name", dto.getLastName());

        String url = "https://api.zoom.us/v2/meetings/" + dto.getMeetingId() + "/registrants";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode json = mapper.readTree(response.body());
        System.out.println("register response: " + json);

        if (response.statusCode() >= 400 || json.has("code")) {
            throw new RuntimeException("Registration failed: " + json.toPrettyString());
        }

//        Map<String, String> result = new HashMap<>();
//        if (json.has("join_url")) result.put("join_url", json.get("join_url").asText());
//        if (json.has("registrant_id")) result.put("registrant_id", json.get("registrant_id").asText());
//        if (json.has("start_time")) result.put("start_time", json.get("start_time").asText());
//        result.put("meeting_id", dto.getMeetingId());

        RegistrantResponse registrantResponse = new RegistrantResponse(
                json.get("join_url").asText(),
                json.get("registrant_id").asText(),
                json.get("start_time").asText()
        );
        return registrantResponse;
    }

    public void deleteMeeting(String accessToken, String meetingId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.zoom.us/v2/meetings/" + meetingId))
                .header("Authorization", "Bearer " + accessToken)
                .DELETE()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete meeting: " + response.body());
        }

        System.out.println("Meeting deleted successfully: " + meetingId);
    }

}