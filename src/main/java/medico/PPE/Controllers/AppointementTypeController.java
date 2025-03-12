package medico.PPE.Controllers;

import medico.PPE.Models.AppointementType;
import medico.PPE.Services.AppointementTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/type")
@CrossOrigin(origins = "http://localhost:4200")
public class AppointementTypeController {
    @Autowired
    private AppointementTypeService service;

    @GetMapping
    public List<AppointementType> getAllAppointments() {
        return service.getAllAppointments();
    }

    @GetMapping("/{id}")
    public AppointementType getAppointmentById(@PathVariable Long id) {
        return service.getAppointmentById(id);
    }

    @PostMapping
    public AppointementType createAppointment(@RequestBody AppointementType appointmenttype) {
        return service.saveAppointment(appointmenttype);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        service.deleteAppointment(id);
    }
    @PutMapping("/{id}")
    public AppointementType updateAppointment(@PathVariable Long id, @RequestBody AppointementType newAppointment) {
        return service.updateAppointment(id, newAppointment);
    }
}
