package medico.PPE.Services;

import medico.PPE.Models.AppointementType;
import medico.PPE.Repositories.AppointementTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointementTypeServiceImpl implements AppointementTypeService {

    @Autowired
    private AppointementTypeRepository repository;

    public List<AppointementType> getAllAppointments() {
        return repository.findAll();
    }

    public AppointementType getAppointmentById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public AppointementType saveAppointment(AppointementType appointment) {
        return repository.save(appointment);
    }
    public AppointementType updateAppointment(Long id, AppointementType newAppointment) {
        Optional<AppointementType> optionalAppointment = repository.findById(id);
        if (optionalAppointment.isPresent()) {
            AppointementType appointment = optionalAppointment.get();
            appointment.setName(newAppointment.getName());
            appointment.setType(newAppointment.getType());
            appointment.setDate(newAppointment.getDate());
            appointment.setDescription(newAppointment.getDescription());
            return repository.save(appointment);
        }
        return null;
    }

    public void deleteAppointment(Long id) {
        repository.deleteById(id);
    }
}
