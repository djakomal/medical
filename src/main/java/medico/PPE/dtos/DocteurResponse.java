package medico.PPE.dtos;

import medico.PPE.Models.Docteur;

public class DocteurResponse {
    private Docteur docteur;
    private String token;

    public DocteurResponse(Docteur docteur, String token) {
        this.docteur = docteur;
        this.token = token;
    }

    // Getters et setters
    public Docteur getDocteur() {
        return docteur;
    }

    public void setDocteur(Docteur docteur) {
        this.docteur = docteur;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
