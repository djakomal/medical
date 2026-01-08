package medico.PPE.Models;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "Appointment")
@EntityListeners(AuditingEntityListener.class)
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private  Long id;

	@Column(name = "name", unique = true)
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "date")
	private String date;
	 
	@Column(name = "time")
	private String time;

	@Column(name = "status")
	private String status;
	
	@Column(name = "description")
	private String description;
	 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="docteur_id",nullable=false)
	private Docteur doctor;
	
	@Column(name = "regtime")
	@Transient
	private String regtime;
	public String getRegtime() {
		return regtime;
	}
	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDate() {
		return date;
	}	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	public  Long getId() {
		return id;
	} 
	public void setDoctor(Docteur doctor){
		this.doctor=doctor;
	}
	public 	Docteur getDoctor(){
		return doctor;
	}

	@Override
	public String toString() {
		return "Appointment [id=" + id + ", name=" + name + ", email=" + email + ", date=" + date + ", time=" + time
				+ ", description=" + description + "]";
				}

}