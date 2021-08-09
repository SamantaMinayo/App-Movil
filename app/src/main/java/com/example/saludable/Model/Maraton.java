package com.example.saludable.Model;

public class Maraton {
    int id;
    public String uid;
    public String time;
    public String date;
    public String maratonimage;
    public String description;
    public String contactname;
    public String contactnumber;
    public String maratontime;
    public String maratondate;
    public String place;
    public String maratonname;
    public String estado;
    public String codigo;
    public String maratondist;
    public String maratontrayectoriaweb;

    public String image;

    public Maraton(String uid, String time, String date, String maratonimage, String description, String contactname, String contactnumber, String maratontime, String maratondate, String place, String maratonname, String codigo, String estado, String distancia, String maratontrayectoriaweb, String image) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.maratonimage = maratonimage;
        this.description = description;
        this.contactname = contactname;
        this.contactnumber = contactnumber;
        this.maratontime = maratontime;
        this.maratondate = maratondate;
        this.place = place;
        this.maratonname = maratonname;
        this.codigo = codigo;
        this.estado = estado;
        this.maratondist = distancia;
        this.maratontrayectoriaweb = maratontrayectoriaweb;
        this.image = image;
    }

    public Maraton(String maratonname, String maratonimagen, String maratondescription, String date, String uid, String image) {
        this.maratonname = maratonname;
        this.maratonimage = maratonimagen;
        this.description = maratondescription;
        this.maratondate = date;
        this.image = image;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public Maraton() {
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Maraton(int id, String uid, String estado) {
        this.id = id;
        this.uid = uid;
        this.estado = estado;
    }

    public String getMaratondist() {
        return maratondist;
    }

    public void setMaratondist(String maratondist) {
        this.maratondist = maratondist;
    }

    public String getMaratontrayectoriaweb() {
        return maratontrayectoriaweb;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMaratonimage() {
        return maratonimage;
    }

    public void setMaratonimage(String maratonimage) {
        this.maratonimage = maratonimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public String getContactnumber() {
        return contactnumber;
    }

    public void setContactnumber(String contactnumber) {
        this.contactnumber = contactnumber;
    }

    public String getMaratontime() {
        return maratontime;
    }

    public void setMaratontime(String maratontime) {
        this.maratontime = maratontime;
    }

    public String getMaratondate() {
        return maratondate;
    }

    public void setMaratondate(String maratondate) {
        this.maratondate = maratondate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMaratonname() {
        return maratonname;
    }

    public void setMaratonname(String maratonname) {
        this.maratonname = maratonname;
    }

    public void setMaratontrayectoriaweb(String maratontrayectoriaweb) {
        this.maratontrayectoriaweb = maratontrayectoriaweb;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}



