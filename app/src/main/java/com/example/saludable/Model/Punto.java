package com.example.saludable.Model;

public class Punto {
    public String distancia;
    public String hora;
    public String latitud;
    public String longitud;
    public String tiempo;
    public String timp;
    public String velocidad;

    public Punto() {
    }


    public Punto(String distancia, String hora, String latitud, String longitud, String tiempo, String timp, String velocidad) {
        this.distancia = distancia;
        this.hora = hora;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tiempo = tiempo;
        this.timp = timp;
        this.velocidad = velocidad;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getTimp() {
        return timp;
    }

    public void setTimp(String timp) {
        this.timp = timp;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }


}
