package medico.PPE.Models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "Appointment")
@EntityListeners(AuditingEntityListener.class)
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private  Long id;
	@Column(name = "email")
	private String email;
	@Column(name = "status")
	private String  status="pending";

	@ManyToOne
    @JoinColumn(name = "patient_id") 
	 @JsonBackReference("customer-appointments") 
    private Customer patient;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="docteur_id",nullable=false)
	@JsonIgnore
	private Docteur doctor;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="creneau_id",nullable=false)
	@JsonIgnore
	private Creneau creneau;
	@Column(name = "regtime")
	private String regtime;
	@Column(name = "firstname")
	private String firstname;
	@Column(name = "lastname")
    private String lastname;
	@Column(name = "birthdate")
    private String birthdate;
	@Column(name = "gender")
    private String gender;
	@Column(name = "appointment_type", nullable = false)
    private String appointmentType; 
	@Column(length = 1024)
	private String zoomJoinUrl;
	@Column(name = "medical_documents", columnDefinition = "LONGTEXT")
    private String medicalDocuments; // Stockage JSON des documents
    
    // Getters et Setters
    public String getMedicalDocuments() { return medicalDocuments; }
    public void setMedicalDocuments(String medicalDocuments) { this.medicalDocuments = medicalDocuments; }
    @Column(length = 64)
    private String zoomMeetingId;
	@Column(name = "phone")
    private String phone;
	@Column(name = "insurance")
    private String insurance;
	@Column(name = "doctorType")
    private String doctorType;
	@Column(name = "otherSpecialist")
    private String otherSpecialist;
	@Column(name = "consent")
    private boolean consent;
	@Column(name = "reason")
	private String reason;
	@Column(name = "symptoms")
	private String symptoms;
	@Column(name = "firstVisit")
	private String firstVisit;
	@Column(name = "allergies")
	private String allergies;
	@Column(name = "medications")
	private String medications;
	@Column(name = "additionalInfo")
	private String additionalInfo;
	@Column(name = "preferredDate")
	private String preferredDate;
	@Column(name = "preferredTime")
	private String preferredTime;
	// Appointment.java — ajouter si absent

	@Column(name = "zoom_start_url", length = 1024)
	private String zoomStartUrl;

	@Column(name = "zoom_password", length = 1024)
	private String zoomPassword;

	@Column(name = "meeting_url", length = 1024)
	private String meetingUrl;

	// Getters & Setters
	public String getZoomMeetingId() { return zoomMeetingId; }
	public void setZoomMeetingId(String zoomMeetingId) { this.zoomMeetingId = zoomMeetingId; }

	public String getZoomJoinUrl() { return zoomJoinUrl; }
	public void setZoomJoinUrl(String zoomJoinUrl) { this.zoomJoinUrl = zoomJoinUrl; }

	public String getZoomStartUrl() { return zoomStartUrl; }
	public void setZoomStartUrl(String zoomStartUrl) { this.zoomStartUrl = zoomStartUrl; }

	public String getZoomPassword() { return zoomPassword; }
	public void setZoomPassword(String zoomPassword) { this.zoomPassword = zoomPassword; }

	public String getMeetingUrl() { return meetingUrl; }
	public void setMeetingUrl(String meetingUrl) { this.meetingUrl = meetingUrl; }

}
