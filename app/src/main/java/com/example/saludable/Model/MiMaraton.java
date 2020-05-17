package com.example.saludable.Model;

public class MiMaraton {
    public String maratonname;
    public String maratonimagen;
    public String maratondescription;
    public String date;

    public MiMaraton(String maratonname, String maratonimagen, String maratondescription, String date) {
        this.maratonname = maratonname;
        this.maratonimagen = maratonimagen;
        this.maratondescription = maratondescription;
        this.date = date;
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



}
