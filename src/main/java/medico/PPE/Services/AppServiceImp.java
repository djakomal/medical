package medico.PPE.Services;
import medico.PPE.Models.Appointment;
import medico.PPE.Models.Creneau;
import medico.PPE.Models.Customer;
import medico.PPE.Models.Docteur;
import medico.PPE.Repositories.AppointmentRepository;
import medico.PPE.Repositories.CreneauRepository;
import medico.PPE.Repositories.CustomerRepository;
import medico.PPE.Repositories.DoctorateRepository;
import medico.PPE.dtos.AppointmentDto;
import medico.PPE.dtos.ZoomResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AppServiceImp implements AppService {

    
    private final  AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final DoctorateRepository doctorateRepository;
    public final CreneauRepository creneauRepository;
    private final ZoomMeetingService zoomMeetingService;
    private final ZoomAuthService zoomAuthService;
    private final String zoomTimezone;

    @Autowired
    public AppServiceImp(
            AppointmentRepository appointmentRepository,
            DoctorateRepository doctorateRepository,
            CreneauRepository creneauRepository,
            CustomerRepository customerRepository,
            ZoomMeetingService zoomMeetingService,
            ZoomAuthService zoomAuthService,
            @Value("${zoom.timezone:UTC}") String zoomTimezone) {
        this.appointmentRepository = appointmentRepository;
        this.doctorateRepository = doctorateRepository;
        this.creneauRepository = creneauRepository;
        this.customerRepository=customerRepository;
        this.zoomMeetingService = zoomMeetingService;
        this.zoomAuthService = zoomAuthService;
        this.zoomTimezone = zoomTimezone;
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAllWithDoctor();  
    }
    @Override
    public Appointment add(AppointmentDto dto) {
        Appointment appointment = new Appointment();
        if (dto.getPatientId() != null) {
        Customer patient = customerRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient non trouvé avec ID: " + dto.getPatientId()));
        appointment.setPatient(patient);
        }
    
        appointment.setFirstname(dto.getFirstname());
        appointment.setLastname(dto.getLastname());
        appointment.setEmail(dto.getEmail());
        appointment.setBirthdate(dto.getBirthdate()); 
        appointment.setPreferredTime(dto.getPreferredTime());
        appointment.setPreferredDate(dto.getPreferredDate());
        appointment.setGender(dto.getGender());
        appointment.setPhone(dto.getPhone());
        appointment.setInsurance(dto.getInsurance());
        appointment.setDoctorType(dto.getDoctorType());
        appointment.setOtherSpecialist(dto.getOtherSpecialist());
        appointment.setConsent(dto.isConsent());
        appointment.setAppointmentType(dto.getAppointmentType());
        appointment.setReason(dto.getReason());
        appointment.setMedicalDocuments(dto.getMedicalDocuments());
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
    
    // Dans AppServiceImp.java
    @Override
    public Appointment updateMedicalDocuments(Long appointmentId, String medicalDocuments) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        appointment.setMedicalDocuments(medicalDocuments);
        return appointmentRepository.save(appointment);
    }
    @Override
    public Appointment getAppById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public Appointment getAppointmentsByPatient(Long patientId){
        return appointmentRepository.findById(patientId).orElse(null);
    }
    
    @Override
    public List<Appointment> getAllAppointmentsByPatient(Long patientId) {
        Customer patient = customerRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID : " + patientId));
        
        return appointmentRepository.findAppointmentsByPatientId(patient.getId());
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

        if (appointment.getZoomMeetingId() == null || appointment.getZoomMeetingId().isBlank()
                || appointment.getZoomJoinUrl() == null || appointment.getZoomJoinUrl().isBlank()) {
                    ZoomResponse meeting = createZoomMeetingForAppointment(appointment);
                    appointment.setZoomMeetingId(meeting.id());
                    appointment.setZoomJoinUrl(meeting.joinUrl());   // ← patient rejoint via ce lien
                    appointment.setZoomStartUrl(meeting.startUrl());
        }
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

    private ZoomResponse createZoomMeetingForAppointment(Appointment appointment) throws Exception {
        String token;
        try {
            token = zoomAuthService.getValidAccessToken();
        } catch (RuntimeException e) {
            throw new RuntimeException("Zoom not authorized. Call GET /api/meetings/authorize then login to Zoom before starting an appointment.", e);
        }

        String topic = buildZoomTopic(appointment);
        LocalDateTime startDateTime = resolveStartDateTime(appointment);
        String startTime = startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        int durationMinutes = resolveDurationMinutes(appointment);

        return zoomMeetingService.createMeeting(token, topic, startTime, durationMinutes, zoomTimezone);
    }

    private String buildZoomTopic(Appointment appointment) {
        String doctorName = null;
        if (appointment.getDoctor() != null) {
            doctorName = firstNonBlank(appointment.getDoctor().getName(), appointment.getDoctor().getUsername());
        }

        String patientName = joinNonBlank(appointment.getFirstname(), appointment.getLastname());
        String fallbackPatient = firstNonBlank(patientName, appointment.getEmail());

        String topic = "Consultation";
        if (doctorName != null) {
            topic += " - Dr " + doctorName;
        }
        if (fallbackPatient != null) {
            topic += " - " + fallbackPatient;
        }

        return topic.length() > 200 ? topic.substring(0, 200) : topic;
    }

    private LocalDateTime resolveStartDateTime(Appointment appointment) {
        LocalDate date = parseFlexibleDate(appointment.getPreferredDate());
        LocalTime time = parseFlexibleTime(appointment.getPreferredTime());

        Creneau creneau = appointment.getCreneau();
        if (date == null && creneau != null && creneau.getDate() != null) {
            date = creneau.getDate();
        }
        if (time == null && creneau != null) {
            time = parseFlexibleTime(creneau.getHeureDebut());
        }

        ZoneId zoneId = safeZoneId(zoomTimezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId).withSecond(0).withNano(0);

        if (date == null || time == null) {
            return now.plusMinutes(1).toLocalDateTime();
        }

        LocalDateTime scheduled = LocalDateTime.of(date, time).withSecond(0).withNano(0);
        if (scheduled.atZone(zoneId).isBefore(now.minusMinutes(1))) {
            return now.plusMinutes(1).toLocalDateTime();
        }

        return scheduled;
    }

    private int resolveDurationMinutes(Appointment appointment) {
        Creneau creneau = appointment.getCreneau();
        if (creneau == null) {
            return 30;
        }

        LocalTime start = parseFlexibleTime(creneau.getHeureDebut());
        LocalTime end = parseFlexibleTime(creneau.getHeureFin());
        if (start == null || end == null) {
            return 30;
        }

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes <= 0 || minutes > 24 * 60) {
            return 30;
        }
        return (int) minutes;
    }

    private static ZoneId safeZoneId(String zoneId) {
        try {
            return ZoneId.of(zoneId);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }

    private static LocalDate parseFlexibleDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }

        for (String pattern : List.of("dd/MM/yyyy", "d/M/yyyy")) {
            try {
                return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private static LocalTime parseFlexibleTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        for (String pattern : List.of("H:mm", "HH:mm", "H:mm:ss", "HH:mm:ss")) {
            try {
                return LocalTime.parse(trimmed, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private static String joinNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(value.trim());
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    @Override
    public Appointment update(Appointment appointment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

   




}
