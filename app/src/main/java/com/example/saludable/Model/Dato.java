package com.example.saludable.Model;

public class Dato {
    public String calorias;
    public String distancia;
    public String genero;
    public String pasos;
    public String rango;
    public String tiempo;
    public String uid;
    public String usuario;
    public String velocidad;
    public String punto;

    public Dato(String calorias, String distancia, String genero, String pasos, String rango, String tiempo, String uid, String usuario, String velocidad, String punto) {
        this.calorias = calorias;
        this.distancia = distancia;
        this.genero = genero;
        this.pasos = pasos;
        this.rango = rango;
        this.tiempo = tiempo;
        this.uid = uid;
        this.usuario = usuario;
        this.velocidad = velocidad;
        this.punto = punto;
    }

    public Dato() {
    }

    public String getCalorias() {
        return calorias;
    }

    public void setCalorias(String calorias) {
        this.calorias = calorias;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getPasos() {
        return pasos;
    }

    public void setPasos(String pasos) {
        this.pasos = pasos;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }

    public String getPunto() {
        return punto;
    }

    public void setPunto(String punto) {
        this.punto = punto;
    }
}
