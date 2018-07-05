package model;

/**
 * Created by idu on 04/07/2018.
 */
public class MeteoDataRecord {

    private int id;
    private String date;
    private float pression;
    private float temperature;
    private float rose;
    private float humidite;
    private String coord;
    private String nom;
    private float lat;
    private float lon;
    private int codeDepartement;
    private String departement;
    private String commune;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getPression() {
        return pression;
    }

    public void setPression(float pression) {
        this.pression = pression;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getRose() {
        return rose;
    }

    public void setRose(float rose) {
        this.rose = rose;
    }

    public float getHumidite() {
        return humidite;
    }

    public void setHumidite(float humidite) {
        this.humidite = humidite;
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public int getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(int codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    @Override
    public String toString() {
        return "MeteoDataRecord{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", pression=" + pression +
                ", temperature=" + temperature +
                ", rose=" + rose +
                ", humidite=" + humidite +
                ", coord='" + coord + '\'' +
                ", nom='" + nom + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
