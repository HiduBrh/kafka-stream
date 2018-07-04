package model;

/**
 * Created by idu on 04/07/2018.
 */
public class Departement {
    private String code;
    private String nom;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Departement{" +
                "code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }
}
