package com.example.saludable;

import java.util.ArrayList;

public class MiMaraton {
    public ArrayList<String> tiempo;
    public ArrayList<String> velocidad;
    public String nombre;
    public String uid;
    public String image;
    public String descripcion;

    public MiMaraton(ArrayList<String> tiempo, ArrayList<String> velocidad, String nombre, String uid, String image, String descripcion) {
        this.tiempo = tiempo;
        this.velocidad = velocidad;
        this.nombre = nombre;
        this.uid = uid;
        this.image = image;
        this.descripcion = descripcion;
    }

    public MiMaraton() {

    }

    public ArrayList<String> getTiempo() {
        return tiempo;
    }

    public void setTiempo(ArrayList<String> tiempo) {
        this.tiempo = tiempo;
    }

    public ArrayList<String> getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(ArrayList<String> velocidad) {
        this.velocidad = velocidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
