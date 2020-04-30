package com.example.saludable.Model;

import java.util.HashMap;

public class Carreras {
    HashMap<String, String> calorias;
    HashMap<String, String> distancia;
    HashMap<String, String> pasos;
    HashMap<String, String> peso;
    HashMap<String, String> tiempo;
    HashMap<String, String> velocidad;

    public Carreras() {
    }

    public Carreras(HashMap<String, String> calorias, HashMap<String, String> distancia, HashMap<String, String> pasos, HashMap<String, String> peso, HashMap<String, String> tiempo, HashMap<String, String> velocidad) {
        this.calorias = calorias;
        this.distancia = distancia;
        this.pasos = pasos;
        this.peso = peso;
        this.tiempo = tiempo;
        this.velocidad = velocidad;
    }

    public HashMap<String, String> getCalorias() {
        return calorias;
    }

    public void setCalorias(HashMap<String, String> calorias) {
        this.calorias = calorias;
    }

    public HashMap<String, String> getDistancia() {
        return distancia;
    }

    public void setDistancia(HashMap<String, String> distancia) {
        this.distancia = distancia;
    }

    public HashMap<String, String> getPasos() {
        return pasos;
    }

    public void setPasos(HashMap<String, String> pasos) {
        this.pasos = pasos;
    }

    public HashMap<String, String> getPeso() {
        return peso;
    }

    public void setPeso(HashMap<String, String> peso) {
        this.peso = peso;
    }

    public HashMap<String, String> getTiempo() {
        return tiempo;
    }

    public void setTiempo(HashMap<String, String> tiempo) {
        this.tiempo = tiempo;
    }

    public HashMap<String, String> getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(HashMap<String, String> velocidad) {
        this.velocidad = velocidad;
    }
}
