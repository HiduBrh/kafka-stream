package model;

/**
 * Created by idu on 04/07/2018.
 */
public class Commune
{
    private String codeDepartement;
    private String nom;
    private String code;
    private Departement departement;

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return "Commune{" +
                "codeDepartement='" + codeDepartement + '\'' +
                ", nom='" + nom + '\'' +
                ", code='" + code + '\'' +
                ", departement=" + departement +
                '}';
    }
}
