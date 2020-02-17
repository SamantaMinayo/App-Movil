package com.example.saludable;

public class Maraton {
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
    public boolean estado;

    public Maraton() {
    }

    public Maraton(String uid, String time, String date, String maratonimage, String description, String contactname, String contactnumber, String maratontime, String maratondate, String place, String maratonname, boolean estado) {
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
        this.estado = estado;
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

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}