package medico.PPE.dtos;

public class zoomDto {

    private String topic;
    private int type;           // 1=instant, 2=scheduled, 3=recurring
    private String startTime;   // ISO 8601: 2025-07-01T15:00:00Z
    private int duration;       // en minutes
    private String timezone;
    private String agenda;
    private Settings settings;

    // ── Classe imbriquée pour les settings ──────────────────────
    public static class Settings {
        private boolean hostVideo;
        private boolean participantVideo;
        private boolean joinBeforeHost;
        private boolean waitingRoom;
        private boolean meetingAuthentication;

        public boolean isHostVideo() { return hostVideo; }
        public void setHostVideo(boolean hostVideo) { this.hostVideo = hostVideo; }

        public boolean isParticipantVideo() { return participantVideo; }
        public void setParticipantVideo(boolean participantVideo) { this.participantVideo = participantVideo; }

        public boolean isJoinBeforeHost() { return joinBeforeHost; }
        public void setJoinBeforeHost(boolean joinBeforeHost) { this.joinBeforeHost = joinBeforeHost; }

        public boolean isWaitingRoom() { return waitingRoom; }
        public void setWaitingRoom(boolean waitingRoom) { this.waitingRoom = waitingRoom; }

        public boolean isMeetingAuthentication() { return meetingAuthentication; }
        public void setMeetingAuthentication(boolean meetingAuthentication) {
            this.meetingAuthentication = meetingAuthentication;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public String getAgenda() { return agenda; }
    public void setAgenda(String agenda) { this.agenda = agenda; }

    public Settings getSettings() { return settings; }
    public void setSettings(Settings settings) { this.settings = settings; }
}