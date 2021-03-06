package model;

import com.sun.org.apache.bcel.internal.generic.FLOAD;

import java.io.Serializable;

/**
 * Created by idu on 05/07/2018.
 */
public class MinMax {

    private float minTemp;
    private float maxTemp;
    private float minPres;
    private float maxPres;
    private String departement;

    public MinMax() {
        this.minTemp = Float.POSITIVE_INFINITY;
        this.maxTemp = Float.NEGATIVE_INFINITY;
        this.minPres = Float.POSITIVE_INFINITY;
        this.maxPres = Float.NEGATIVE_INFINITY;
    }


    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMinPres() {
        return minPres;
    }

    public void setMinPres(float minPres) {
        this.minPres = minPres;
    }

    public float getMaxPres() {
        return maxPres;
    }

    public void setMaxPres(float maxPres) {
        this.maxPres = maxPres;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return "MinMax{" +
                "minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", minPres=" + minPres +
                ", maxPres=" + maxPres +
                ", departement='" + departement + '\'' +
                '}';
    }
}
