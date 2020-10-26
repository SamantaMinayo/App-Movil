package com.example.saludable.Model;

public class UsrMrtn {
    int id;
    String uid;
    String estado;

    public UsrMrtn(int id, String uid, String estado) {
        this.id = id;
        this.uid = uid;
        this.estado = estado;
    }

    public UsrMrtn() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
