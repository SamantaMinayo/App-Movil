package com.example.saludable.Model;

public class MiResultado {
    public String uid;
    public String calorias;
    public String distancia;
    public String genero;
    public String pasos;
    public String rango;
    public String tiempo;
    public String tiempomed;
    public String usuario;
    public String velocidad;
    public String velocidadmed;
    public String velmed;
    public String velmin;
    public String velmax;


    public MiResultado(String uid, String calorias, String distancia, String genero, String pasos, String rango, String tiempo, String tiempomed, String usuario, String velocidad, String velocidadmed, String velmed, String velmin, String velmax) {
        this.uid = uid;
        this.calorias = calorias;
        this.distancia = distancia;
        this.genero = genero;
        this.pasos = pasos;
        this.rango = rango;
        this.tiempo = tiempo;
        this.tiempomed = tiempomed;
        this.usuario = usuario;
        this.velocidad = velocidad;
        this.velocidadmed = velocidadmed;
        this.velmed = velmed;
        this.velmin = velmin;
        this.velmax = velmax;
    }

    public MiResultado(String uid, String tiempo, String distancia, String pasos, String velmax, String velmed, String velmin, String calorias) {
        this.uid = uid;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.pasos = pasos;
        this.velmax = velmax;
        this.velmed = velmed;
        this.velmin = velmin;
        this.calorias = calorias;
    }

    public MiResultado() {
    }


    public String getVelmin() {
        return velmin;
    }

    public void setVelmin(String velmin) {
        this.velmin = velmin;
    }

    public String getVelmax() {
        return velmax;
    }

    public void setVelmax(String velmax) {
        this.velmax = velmax;
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

    public String getTiempomed() {
        return tiempomed;
    }

    public void setTiempomed(String tiempomed) {
        this.tiempomed = tiempomed;
    }

    public String getVelocidadmed() {
        return velocidadmed;
    }

    public void setVelocidadmed(String velocidadmed) {
        this.velocidadmed = velocidadmed;
    }

    public String getVelmed() {
        return velmed;
    }

    public void setVelmed(String velmed) {
        this.velmed = velmed;
    }


}
