package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saludable.Model.MaratonResult;

public class DaoMarRes {

    SQLiteDatabase db;
    MaratonResult maratonResult;
    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists maresultados(" +
            "uid text primary key, pasos text,velmax text,velmed text," +
            "velmin text,calorias text,maxritmo text,minritmo text," +
            "ritmo text,mejtime text,peortime text,time text)";

    public DaoMarRes(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(MaratonResult res) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", res.getUid () );
        contenedor.put ( "pasos", res.getPasos () );
        contenedor.put ( "velmax", res.getVelmax () );
        contenedor.put ( "velmed", res.getVelmed () );
        contenedor.put ( "velmin", res.getVelmin () );
        contenedor.put ( "calorias", res.getCalorias () );
        contenedor.put ( "maxritmo", res.getMaxritmo () );
        contenedor.put ( "minritmo", res.getMinritmo () );
        contenedor.put ( "ritmo", res.getRitmo () );
        contenedor.put ( "mejtime", res.getMejtime () );
        contenedor.put ( "peortime", res.getPeortime () );
        contenedor.put ( "time", res.getTime () );
        return (db.insert ( "maresultados", null, contenedor )) > 0;
    }

    public boolean Eliminar(String uid) {
        return true;
    }

    public boolean Editar(MaratonResult res) {
        ContentValues contenedor = new ContentValues ();
        if (!res.getUid ().isEmpty ()) {
            contenedor.put ( "uid", res.getUid () );
        }
        if (!res.getPasos ().isEmpty ()) {
            contenedor.put ( "pasos", res.getPasos () );
        }
        if (!res.getVelmax ().isEmpty ()) {
            contenedor.put ( "velmax", res.getVelmax () );
        }
        if (!res.getVelmed ().isEmpty ()) {
            contenedor.put ( "velmed", res.getVelmed () );
        }
        if (!res.getVelmin ().isEmpty ()) {
            contenedor.put ( "velmin", res.getVelmin () );
        }
        if (!res.getCalorias ().isEmpty ()) {
            contenedor.put ( "calorias", res.getCalorias () );
        }
        if (!res.getMaxritmo ().isEmpty ()) {
            contenedor.put ( "maxritmo", res.getMaxritmo () );
        }
        if (!res.getMinritmo ().isEmpty ()) {
            contenedor.put ( "minritmo", res.getMinritmo () );
        }
        if (!res.getRitmo ().isEmpty ()) {
            contenedor.put ( "ritmo", res.getRitmo () );
        }
        if (!res.getMejtime ().isEmpty ()) {
            contenedor.put ( "mejtime", res.getMejtime () );
        }
        if (!res.getPeortime ().isEmpty ()) {
            contenedor.put ( "peortime", res.getPeortime () );
        }
        if (!res.getTime ().isEmpty ()) {
            contenedor.put ( "time", res.getTime () );
        }
        return (db.update ( "maresultados", contenedor, "uid=" + "'" + res.getUid () + "'", null )) > 0;
    }

    public MaratonResult ObtenerMaratonRes(String id) {
        Cursor cursor = db.rawQuery ( "select *from maresultados where uid=" + "'" + id + "'", null );
        cursor.moveToFirst ();
        if (cursor.getCount () > 0) {
            maratonResult = new MaratonResult ( cursor.getString ( 0 ),
                    cursor.getString ( 1 ),
                    cursor.getString ( 2 ),
                    cursor.getString ( 3 ),
                    cursor.getString ( 4 ),
                    cursor.getString ( 5 ),
                    cursor.getString ( 6 ),
                    cursor.getString ( 7 ),
                    cursor.getString ( 8 ),
                    cursor.getString ( 9 ),
                    cursor.getString ( 10 ),
                    cursor.getString ( 11 )
            );
            return maratonResult;
        } else {
            return null;
        }

    }


}
