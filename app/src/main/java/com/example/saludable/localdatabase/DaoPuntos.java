package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saludable.Model.Punto;

import java.util.ArrayList;

public class DaoPuntos {

    SQLiteDatabase db;
    Punto point;
    ArrayList<Punto> lista = new ArrayList<Punto> ();
    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists puntos(" +
            "id Integer primary key," +
            "uid text," +
            "carrera text," +
            "distancia text," +
            "hora text," +
            "latitud text," +
            "longitud text," +
            "tiempo text," +
            "timp text," +
            "velocidad text)";

    public DaoPuntos(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(Punto pnt) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", pnt.getUid () );
        contenedor.put ( "carrera", pnt.getCarrera () );
        contenedor.put ( "distancia", pnt.getDistancia () );
        contenedor.put ( "hora", pnt.getHora () );
        contenedor.put ( "latitud", pnt.getLatitud () );
        contenedor.put ( "longitud", pnt.getLongitud () );
        contenedor.put ( "tiempo", pnt.getTiempo () );
        contenedor.put ( "timp", pnt.getTimp () );
        contenedor.put ( "velocidad", pnt.getVelocidad () );

        return (db.insert ( "puntos", null, contenedor )) > 0;
    }

    public boolean Eliminar(String uid) {
        return (db.delete ( "puntos", "uid=" + "'" + uid + "'", null )) > 0;
    }

    public boolean Editar(Punto pnt) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", pnt.getUid () );
        contenedor.put ( "carrera", pnt.getCarrera () );
        contenedor.put ( "distancia", pnt.getDistancia () );
        contenedor.put ( "hora", pnt.getHora () );
        contenedor.put ( "latitud", pnt.getLatitud () );
        contenedor.put ( "longitud", pnt.getLongitud () );
        contenedor.put ( "tiempo", pnt.getTiempo () );
        contenedor.put ( "timp", pnt.getTimp () );
        contenedor.put ( "velocidad", pnt.getVelocidad () );
        return (db.update ( "puntos", contenedor, "uid=" + "'" + pnt.getId () + "'", null )) > 0;
    }

    public ArrayList<Punto> ObtenerPuntos(String uidcar) {
        lista.clear ();
        Cursor cursor = db.rawQuery ( "select *from puntos where carrera=" + "'" + uidcar + "'", null );
        if (cursor != null && cursor.getCount () > 0) {
            cursor.moveToFirst ();
            do {
                lista.add ( new Punto ( cursor.getInt ( 0 ),
                        cursor.getString ( 1 ),
                        cursor.getString ( 2 ),
                        cursor.getString ( 3 ),
                        cursor.getString ( 4 ),
                        cursor.getString ( 5 ),
                        cursor.getString ( 6 ),
                        cursor.getString ( 7 ),
                        cursor.getString ( 8 ),
                        cursor.getString ( 9 ) ) );
            } while (cursor.moveToNext ());
            return lista;
        } else {
            return null;
        }
    }

}
