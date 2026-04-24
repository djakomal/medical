package medico.PPE.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {

private  Long id;

private String email;

private String status;
private Long patientId;

private Long doctorId;
private Long creneauId;
private String regtime;

private String firstname;

private String lastname;

private String birthdate;

private String gender;

private String phone;

private String insurance;

private String doctorType;
private String appointmentType;
private String otherSpecialist;
private String medicalDocuments;
private boolean consent;

private String reason;

private String symptoms;

private String firstVisit;

private String allergies;

private String medications;

private String additionalInfo;
private String preferredTime;
private String preferredDate;
private String meetingUrl;
private String zoomMeetingId;
private String zoomStartUrl;
private String zoomPassword;

// Getters & Setters
public String getStatus() { return status; }
public void setStatus(String status) { this.status = status; }

public String getMeetingUrl() { return meetingUrl; }
public void setMeetingUrl(String meetingUrl) { this.meetingUrl = meetingUrl; }

public String getZoomMeetingId() { return zoomMeetingId; }
public void setZoomMeetingId(String zoomMeetingId) { this.zoomMeetingId = zoomMeetingId; }

public String getZoomStartUrl() { return zoomStartUrl; }
public void setZoomStartUrl(String zoomStartUrl) { this.zoomStartUrl = zoomStartUrl; }

public String getZoomPassword() { return zoomPassword; }
public void setZoomPassword(String zoomPassword) { this.zoomPassword = zoomPassword; }
}
