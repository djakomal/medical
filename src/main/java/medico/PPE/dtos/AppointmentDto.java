package medico.PPE.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {

private  Long id;

private String email;

private String status;


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

private String otherSpecialist;

private boolean consent;

private String reason;

private String symptoms;

private String firstVisit;

private String allergies;

private String medications;

private String additionalInfo;
private String preferredTime;
private String preferredDate;



}
