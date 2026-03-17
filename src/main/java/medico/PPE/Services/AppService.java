package medico.PPE.Services;

import medico.PPE.Models.Appointment;
import medico.PPE.dtos.AppointmentDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AppService {
    List<Appointment> getAll();

    Appointment add(AppointmentDto dto);

   // AppointmentDto update(Long Id, AppointmentDto appointment) throws Exception;

    Appointment getAppById(Long id);
    
    Appointment validateAppointment(Long id) throws Exception;

    Appointment rejectAppointment(Long id) throws Exception;

    Appointment startAppointment(Long id) throws Exception;

    void delete(Long id);

    List<Appointment> getAppointmentByDoctor(Long doctorId);
    
    List<Appointment> getAppointmentByEmail(String email);

    Appointment getAppointmentsByPatient(Long patientId);

    List<Appointment> getAllAppointmentsByPatient(Long patientId);
}
