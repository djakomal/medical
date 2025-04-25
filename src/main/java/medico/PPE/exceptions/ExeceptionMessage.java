package medico.PPE.exceptions;

public class ExeceptionMessage {
    //Global
    public static String ACCES_DATA_WARNING = "WARNING: liste indisponible";

    // Billing message

    public static String Appointment_UPDATE_FAILED = "ERROR: echec de modification des information de Appointment";

    public static String Appointment_UPDATE_FAILED_BY_ID = "ERROR: echec de modification des information de Appointment du a l'abscence de l'id";
    public static String Appointment_SAVE_FAILED = "ERROR: echec d'enregistrement d'un Appointment";

    // billing  type message

    public static String Docteur = "ERROR: D'ajout d'un utilisateur ";
    public static String Billing_TYPE_SAVE_FAILED = "ERROR: echec d'enregistrement d'un type billing";
    public static String Billing_TYPE_UPDATE_FAILED_BY_ID = "ERROR: echec de modification des information de Billing du a l'abscence de l'id";

    public static String User_UPDATE_FAILED = "ERROR: echec de modification des information d'un  user ";
    public static String User_SAVE_FAILED = "ERROR: echec d'enregistrement d'un user";
    public static String User_UPDATE_FAILED_BY_ID = "ERROR: echec de modification des information de user du a l'abscence de l'id";


    public static String Connexion_FAILED = "connexion echouer ";


}
