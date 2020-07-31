package com.example.saludable.Model;


public class User {

    public String uid;
    public String email;
    public String altura;
    public String edad;
    public String peso;
    public String genero;
    public String fullname;
    public String username;
    public String country;
    public String imc;
    public String profileimage;
    public String status;

    public User() {
    }

    public User(String uid, String email, String altura, String edad, String peso, String genero, String fullname, String username, String country, String imc, String profileimage, String status) {
        this.uid = uid;
        this.email = email;
        this.altura = altura;
        this.edad = edad;
        this.peso = peso;
        this.genero = genero;
        this.fullname = fullname;
        this.username = username;
        this.country = country;
        this.imc = imc;
        this.profileimage = profileimage;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImc() {
        return imc;
    }

    public void setImc(String imc) {
        this.imc = imc;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
