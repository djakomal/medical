package medico.PPE.Models;


import jakarta.persistence.*;
import lombok.Data;
import medico.PPE.Enums.UserRole;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "email", unique=true)

	private String email;
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "username")

	private String username;
	@Column(name = "password")
	@Transient
	private String password;
	
	@Column(name = "first_name")

	private String firstName;
	
	@Column(name = "last_name")

	private String lastName;
	
	@Column(name = "enabled")
	private boolean enabled;

	@Column(name = "gender")
	private String gender;


	@Enumerated(EnumType.STRING)
	private UserRole role=UserRole.valueOf("USER");

	@Column(name = "lastseen")
	@Transient
	private String lastseen;
	public String getLastseen() {
		return lastseen;
	}
	public void setLastseen(String lastseen) {
		this.lastseen = lastseen;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = UserRole.valueOf(role);
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(boolean value) {
		this.enabled = value;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ",username="+username+ ", email=" + email + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", enabled=" + enabled +
				", gender=" + gender + ", role=" + role + ", lastseen=" + lastseen + "]";
	}

}