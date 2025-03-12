package medico.PPE.Services;

import medico.PPE.Models.AppointementType;

import java.util.List;

public interface AppointementTypeService {
    List<AppointementType> getAllAppointments();

    AppointementType getAppointmentById(Long id);

    AppointementType saveAppointment(AppointementType appointmenttype);

    void deleteAppointment(Long id);

    AppointementType updateAppointment(Long id, AppointementType newAppointment);
}
