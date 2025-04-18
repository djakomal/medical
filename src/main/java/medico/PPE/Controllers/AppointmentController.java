package medico.PPE.Controllers;

import medico.PPE.Models.Appointment;
import medico.PPE.Services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/appointment")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    @Autowired
    private AppService appService;

    @GetMapping("")
    public List<Appointment> getAll() {
        return appService.getAll();

    }
    @PostMapping("/add")
    public Appointment add(@RequestBody Appointment appointment){

        return  appService.add(appointment);
    }



/*    @PutMapping("/update/{Id}")
    public AppointmentDto update(@PathVariable Long Id, @RequestBody AppointmentDto appointment) throws Exception{
        AppointmentDto updatedappointment = appService.update(Id,appointment);
        return  appService.update(Id, updatedappointment);
    }*/

/*    @DeleteMapping("/delete/{taxeId}")
    public  void delete(@PathVariable Long taxeId){
        taxesService.delete(taxeId);
    }*/

    @GetMapping("/get/{Id}")
    public Appointment getAppById(@PathVariable Long Id){
        return  appService.getAppById(Id);
    }

}
