package com.example.saludable;

public class Maraton {
    public String uid;
    public String time;
    public String date;
    public String maratonimage;
    public String description;
    public String contactname;
    public String contactnumber;
    public String time_maraton;
    public String date_maraton;
    public String lugar;
    public String namecarrera;

    public Maraton(String uid, String time, String date, String maratonimage, String description, String contactname, String contactnumbre, String time_maraton, String date_maraton, String lugar, String namecarrera) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.maratonimage = maratonimage;
        this.description = description;
        this.contactname = contactname;
        this.contactnumber = contactnumbre;
        this.time_maraton = time_maraton;
        this.date_maraton = date_maraton;
        this.lugar = lugar;
        this.namecarrera = namecarrera;
    }

    public Maraton() {

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

    public String getTime_maraton() {
        return time_maraton;
    }

    public void setTime_maraton(String time_maraton) {
        this.time_maraton = time_maraton;
    }

    public String getDate_maraton() {
        return date_maraton;
    }

    public void setDate_maraton(String date_maraton) {
        this.date_maraton = date_maraton;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getNamecarrera() {
        return namecarrera;
    }

    public void setNamecarrera(String namecarrera) {
        this.namecarrera = namecarrera;
    }

}