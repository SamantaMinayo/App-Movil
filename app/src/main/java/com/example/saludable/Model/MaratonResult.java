package com.example.saludable.Model;

public class MaratonResult {
    String uid;
    String pasos;
    String velmax;
    String velmed;
    String velmin;
    String calorias;
    String maxritmo;
    String minritmo;
    String ritmo;
    String mejtime;
    String peortime;
    String time;

    public MaratonResult(String uid, String pasos, String velmax, String velmed, String velmin, String calorias, String maxritmo, String minritmo, String ritmo, String mejtime, String peortime, String time) {
        this.uid = uid;
        this.pasos = pasos;
        this.velmax = velmax;
        this.velmed = velmed;
        this.velmin = velmin;
        this.calorias = calorias;
        this.maxritmo = maxritmo;
        this.minritmo = minritmo;
        this.ritmo = ritmo;
        this.mejtime = mejtime;
        this.peortime = peortime;
        this.time = time;
    }

    public MaratonResult() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPasos() {
        return pasos;
    }

    public void setPasos(String pasos) {
        this.pasos = pasos;
    }

    public String getVelmax() {
        return velmax;
    }

    public void setVelmax(String velmax) {
        this.velmax = velmax;
    }

    public String getVelmed() {
        return velmed;
    }

    public void setVelmed(String velmed) {
        this.velmed = velmed;
    }

    public String getVelmin() {
        return velmin;
    }

    public void setVelmin(String velmin) {
        this.velmin = velmin;
    }

    public String getCalorias() {
        return calorias;
    }

    public void setCalorias(String calorias) {
        this.calorias = calorias;
    }

    public String getMaxritmo() {
        return maxritmo;
    }

    public void setMaxritmo(String maxritmo) {
        this.maxritmo = maxritmo;
    }

    public String getMinritmo() {
        return minritmo;
    }

    public void setMinritmo(String minritmo) {
        this.minritmo = minritmo;
    }

    public String getRitmo() {
        return ritmo;
    }

    public void setRitmo(String ritmo) {
        this.ritmo = ritmo;
    }

    public String getMejtime() {
        return mejtime;
    }

    public void setMejtime(String mejtime) {
        this.mejtime = mejtime;
    }

    public String getPeortime() {
        return peortime;
    }

    public void setPeortime(String peortime) {
        this.peortime = peortime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
