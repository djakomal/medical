package medico.PPE.Services;
import medico.PPE.Models.Appointment;
import medico.PPE.Models.Creneau;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.AppointmentRepository;
import medico.PPE.Repositories.CreneauRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.dtos.AppointmentDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppServiceImp implements AppService {

    
    private final  AppointmentRepository appointmentRepository;
    
    private final DoctorateRepository doctorateRepository;
    public final CreneauRepository creneauRepository;

    @Autowired
    public AppServiceImp(AppointmentRepository appointmentRepository, DoctorateRepository doctorateRepository, CreneauRepository creneauRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorateRepository = doctorateRepository;
        this.creneauRepository = creneauRepository;
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAllWithDoctor();  //  Évite le N+1 problem
    }

    @Override
    public Appointment add(AppointmentDto dto) {
        Appointment appointment = new Appointment();
        appointment.setFirstname(dto.getFirstname());
        appointment.setLastname(dto.getLastname());
        appointment.setEmail(dto.getEmail());
        appointment.setBirthdate(dto.getBirthdate()); // ou preferredDate selon usage
        appointment.setPreferredTime(dto.getPreferredTime());
        appointment.setPreferredDate(dto.getPreferredDate());
        appointment.setGender(dto.getGender());
        appointment.setPhone(dto.getPhone());
        appointment.setInsurance(dto.getInsurance());
        appointment.setDoctorType(dto.getDoctorType());
        appointment.setOtherSpecialist(dto.getOtherSpecialist());
        appointment.setConsent(dto.isConsent());
        appointment.setReason(dto.getReason());
        appointment.setSymptoms(dto.getSymptoms());
        appointment.setFirstVisit(dto.getFirstVisit());
        appointment.setAllergies(dto.getAllergies());
        appointment.setMedications(dto.getMedications());
        appointment.setAdditionalInfo(dto.getAdditionalInfo());

        appointment.setStatus("pending");

    
        Docteur doctor = doctorateRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        appointment.setDoctor(doctor);
    
        Creneau creneau = creneauRepository.findById(dto.getCreneauId())
                .orElseThrow(() -> new RuntimeException("Crénau non trouvé"));
        appointment.setCreneau(creneau);
    
        // Infos médicales
        if(dto.getReason() != null) {
            appointment.setReason(dto.getReason());
            // autres champs à mapper si tu les ajoutes dans Appointment
        }
    
        return appointmentRepository.save(appointment);
    }
    


    @Override
    public Appointment getAppById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }
    
    @Override
    public Appointment validateAppointment(Long id) throws Exception {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new Exception("Appointment not found with id: " + id));
        
        appointment.setStatus("validated");
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment rejectAppointment(Long id) throws Exception {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new Exception("Appointment not found with id: " + id));
        
        appointment.setStatus("rejected");
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment startAppointment(Long id) throws Exception {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new Exception("Appointment not found with id: " + id));
        
        appointment.setStatus("started");
        return appointmentRepository.save(appointment);
    }

    @Override
    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointment> getAppointmentByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctor_Id(doctorId);  
    }
    
    @Override
    public List<Appointment> getAppointmentByEmail(String email) {
        return appointmentRepository.findByEmail(email);  
    }
}
