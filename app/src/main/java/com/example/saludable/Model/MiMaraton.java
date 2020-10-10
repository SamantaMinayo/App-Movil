package com.example.saludable.Model;

public class MiMaraton {
    public String maratonname;
    public String maratonimagen;
    public String maratondescription;
    public String date;
    public String uid;

    public MiMaraton(String maratonname, String maratonimagen, String maratondescription, String date, String uid) {
        this.maratonname = maratonname;
        this.maratonimagen = maratonimagen;
        this.maratondescription = maratondescription;
        this.date = date;
        this.uid = uid;
    }

    public MiMaraton() {
    }

    public String getMaratonname() {
        return maratonname;
    }

    public void setMaratonname(String maratonname) {
        this.maratonname = maratonname;
    }

    public String getMaratonimagen() {
        return maratonimagen;
    }

    public void setMaratonimagen(String maratonimagen) {
        this.maratonimagen = maratonimagen;
    }

    public String getMaratondescription() {
        return maratondescription;
    }

    public void setMaratondescription(String maratondescription) {
        this.maratondescription = maratondescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



}
