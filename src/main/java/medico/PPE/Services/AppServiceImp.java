package medico.PPE.Services;

import medico.PPE.Models.Appointment;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.AppointmentRepository;
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

    @Autowired
    public AppServiceImp(AppointmentRepository appointmentRepository, DoctorateRepository doctorateRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorateRepository = doctorateRepository;
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAllWithDoctor();  // ✅ Évite le N+1 problem
    }

    @Override
    public Appointment add(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("appointment cannot be null");
        }
    
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor must be specified for appointment");
        }
    
        Long doctorId = appointment.getDoctor().getId();
        System.out.println("🔍 Doctor ID reçu: " + doctorId);
        
        Docteur doctor = doctorateRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec ID: " + doctorId));
        
        System.out.println("✅ Docteur trouvé: " + doctor.getUsername());
        appointment.setDoctor(doctor);
        
        Appointment saved = appointmentRepository.save(appointment);
        System.out.println("💾 Rendez-vous sauvegardé - ID: " + saved.getId() + ", Doctor ID: " + saved.getDoctor().getId());
        
        return saved;
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
