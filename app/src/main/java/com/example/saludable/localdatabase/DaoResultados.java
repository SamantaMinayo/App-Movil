package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saludable.Model.MiResultado;

import java.util.ArrayList;

public class DaoResultados {

    SQLiteDatabase db;
    MiResultado resultado;
    ArrayList<MiResultado> lista = new ArrayList<MiResultado> ();

    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists resultados(" +
            "uid text primary key,mduracion text,mdistancia text,mpasos text," +
            "mvelmax text,mvelmed text,mvelmin text,mcalorias text)";

    public DaoResultados(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(MiResultado res) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", res.getUid () );
        contenedor.put ( "mduracion", res.getTiempo () );
        contenedor.put ( "mdistancia", res.getDistancia () );
        contenedor.put ( "mpasos", res.getPasos () );
        contenedor.put ( "mvelmax", res.getVelmax () );
        contenedor.put ( "mvelmed", res.getVelmed () );
        contenedor.put ( "mvelmin", res.getVelmin () );
        contenedor.put ( "mcalorias", res.getCalorias () );
        return (db.insert ( "resultados", null, contenedor )) > 0;
    }

    public boolean Eliminar(String uid) {
        return true;
    }

    public boolean Editar(MiResultado res) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", res.getUid () );
        if (!res.getTiempo ().isEmpty ()) {
            contenedor.put ( "mduracion", res.getTiempo () );
        }
        if (!res.getDistancia ().isEmpty ()) {
            contenedor.put ( "mdistancia", res.getDistancia () );
        }
        if (!res.getPasos ().isEmpty ()) {
            contenedor.put ( "mpasos", res.getPasos () );
        }
        if (!res.getVelmax ().isEmpty ()) {
            contenedor.put ( "mvelmax", res.getVelmax () );
        }
        if (!res.getVelmed ().isEmpty ()) {
            contenedor.put ( "mvelmed", res.getVelmed () );
        }
        if (!res.getVelmin ().isEmpty ()) {
            contenedor.put ( "mvelmin", res.getVelmin () );
        }
        if (!res.getCalorias ().isEmpty ()) {
            contenedor.put ( "mcalorias", res.getCalorias () );
        }
        return (db.update ( "resultados", contenedor, "uid=" + "'" + res.getUid () + "'", null )) > 0;
    }

    public MiResultado ObtenerResultado(String id) {
        Cursor cursor = db.rawQuery ( "select *from resultados where uid=" + "'" + id + "'", null );
        cursor.moveToFirst ();
        if (cursor.getCount () > 0) {
            resultado = new MiResultado ( cursor.getString ( 0 ),
                    cursor.getString ( 1 ),
                    cursor.getString ( 2 ),
                    cursor.getString ( 3 ),
                    cursor.getString ( 4 ),
                    cursor.getString ( 5 ),
                    cursor.getString ( 6 ),
                    cursor.getString ( 7 )
            );
            return resultado;
        } else {
            return null;
        }
    }

    public ArrayList<MiResultado> ObtenerDatos() {
        lista.clear ();
        Cursor cursor = db.rawQuery ( "select *from resultados ", null );
        if (cursor != null && cursor.getCount () > 0) {
            cursor.moveToFirst ();
            do {
                lista.add ( new MiResultado (
                        cursor.getString ( 0 ),
                        cursor.getString ( 1 ),
                        cursor.getString ( 2 ),
                        cursor.getString ( 3 ),
                        cursor.getString ( 4 ),
                        cursor.getString ( 5 ),
                        cursor.getString ( 6 ),
                        cursor.getString ( 7 ) ) );
            } while (cursor.moveToNext ());
            return lista;
        } else {
            return null;
        }
    }


}


