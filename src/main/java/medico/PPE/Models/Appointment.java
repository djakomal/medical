package medico.PPE.Models;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @JoinColumn(name = "patient_id")  // Nom de la colonne dans la table appointments
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

	private String zoomJoinUrl;
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
	

}