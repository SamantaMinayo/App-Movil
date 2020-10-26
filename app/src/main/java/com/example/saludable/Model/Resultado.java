package com.example.saludable.Model;

public class Resultado {
    String uid;
    String mduracion;
    String mdistancia;
    String mpasos;
    String mvelmax;
    String mvelmed;
    String mvelmin;
    String mcalorias;
    String mpuntomax;
    String mpuntomin;
    String mritmo;

    public Resultado(String uid, String mduracion, String mdistancia, String mpasos, String mvelmax, String mvelmed, String mvelmin, String mcalorias, String mpuntomax, String mpuntomin, String mritmo) {
        this.uid = uid;
        this.mduracion = mduracion;
        this.mdistancia = mdistancia;
        this.mpasos = mpasos;
        this.mvelmax = mvelmax;
        this.mvelmed = mvelmed;
        this.mvelmin = mvelmin;
        this.mcalorias = mcalorias;
        this.mpuntomax = mpuntomax;
        this.mpuntomin = mpuntomin;
        this.mritmo = mritmo;
    }

    public Resultado() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMduracion() {
        return mduracion;
    }

    public void setMduracion(String mduracion) {
        this.mduracion = mduracion;
    }

    public String getMdistancia() {
        return mdistancia;
    }

    public void setMdistancia(String mdistancia) {
        this.mdistancia = mdistancia;
    }

    public String getMpasos() {
        return mpasos;
    }

    public void setMpasos(String mpasos) {
        this.mpasos = mpasos;
    }

    public String getMvelmax() {
        return mvelmax;
    }

    public void setMvelmax(String mvelmax) {
        this.mvelmax = mvelmax;
    }

    public String getMvelmed() {
        return mvelmed;
    }

    public void setMvelmed(String mvelmed) {
        this.mvelmed = mvelmed;
    }

    public String getMvelmin() {
        return mvelmin;
    }

    public void setMvelmin(String mvelmin) {
        this.mvelmin = mvelmin;
    }

    public String getMcalorias() {
        return mcalorias;
    }

    public void setMcalorias(String mcalorias) {
        this.mcalorias = mcalorias;
    }

    public String getMpuntomax() {
        return mpuntomax;
    }

    public void setMpuntomax(String mpuntomax) {
        this.mpuntomax = mpuntomax;
    }

    public String getMpuntomin() {
        return mpuntomin;
    }

    public void setMpuntomin(String mpuntomin) {
        this.mpuntomin = mpuntomin;
    }

    public String getMritmo() {
        return mritmo;
    }

    public void setMritmo(String mritmo) {
        this.mritmo = mritmo;
    }

}

