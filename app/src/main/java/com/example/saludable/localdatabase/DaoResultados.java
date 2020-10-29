package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saludable.Model.Dato;
import com.example.saludable.Model.Resultado;

import java.util.ArrayList;

public class DaoResultados {

    SQLiteDatabase db;
    Resultado resultado;
    ArrayList<Dato> lista = new ArrayList<Dato> ();

    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists resultados(" +
            "uid text primary key,mduracion text,mdistancia text,mpasos text," +
            "mvelmax text,mvelmed text,mvelmin text,mcalorias text," +
            "mpuntomax text,mpuntomin text,mritmo text)";

    public DaoResultados(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(Resultado res) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", res.getUid () );
        contenedor.put ( "mduracion", res.getMduracion () );
        contenedor.put ( "mdistancia", res.getMdistancia () );
        contenedor.put ( "mpasos", res.getMpasos () );
        contenedor.put ( "mvelmax", res.getMvelmax () );
        contenedor.put ( "mvelmed", res.getMvelmed () );
        contenedor.put ( "mvelmin", res.getMvelmin () );
        contenedor.put ( "mcalorias", res.getMcalorias () );
        contenedor.put ( "mpuntomax", res.getMpuntomax () );
        contenedor.put ( "mpuntomin", res.getMpuntomin () );
        contenedor.put ( "mritmo", res.getMritmo () );
        return (db.insert ( "resultados", null, contenedor )) > 0;
    }

    public boolean Eliminar(String uid) {
        return true;
    }

    public boolean Editar(Resultado res) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", res.getUid () );
        if (!res.getMduracion ().isEmpty ()) {
            contenedor.put ( "mduracion", res.getMduracion () );
        }
        if (!res.getMdistancia ().isEmpty ()) {
            contenedor.put ( "mdistancia", res.getMdistancia () );
        }
        if (!res.getMpasos ().isEmpty ()) {
            contenedor.put ( "mpasos", res.getMpasos () );
        }
        if (!res.getMvelmax ().isEmpty ()) {
            contenedor.put ( "mvelmax", res.getMvelmax () );
        }
        if (!res.getMvelmed ().isEmpty ()) {
            contenedor.put ( "mvelmed", res.getMvelmed () );
        }
        if (!res.getMvelmin ().isEmpty ()) {
            contenedor.put ( "mvelmin", res.getMvelmin () );
        }
        if (!res.getMcalorias ().isEmpty ()) {
            contenedor.put ( "mcalorias", res.getMcalorias () );
        }
        if (!res.getMpuntomax ().isEmpty ()) {
            contenedor.put ( "mpuntomax", res.getMpuntomax () );
        }
        if (!res.getMpuntomin ().isEmpty ()) {
            contenedor.put ( "mpuntomin", res.getMpuntomin () );

        }
        if (!res.getMritmo ().isEmpty ()) {
            contenedor.put ( "mritmo", res.getMritmo () );
        }


        return (db.update ( "resultados", contenedor, "uid=" + "'" + res.getUid () + "'", null )) > 0;
    }

    public Resultado ObtenerResultado(String id) {
        Cursor cursor = db.rawQuery ( "select *from resultados where uid=" + "'" + id + "'", null );
        cursor.moveToFirst ();
        if (cursor.getCount () > 0) {
            resultado = new Resultado ( cursor.getString ( 0 ),
                    cursor.getString ( 1 ),
                    cursor.getString ( 2 ),
                    cursor.getString ( 3 ),
                    cursor.getString ( 4 ),
                    cursor.getString ( 5 ),
                    cursor.getString ( 6 ),
                    cursor.getString ( 7 ),
                    cursor.getString ( 8 ),
                    cursor.getString ( 9 ),
                    cursor.getString ( 10 )
            );
            return resultado;
        } else {
            return null;
        }
    }

    public ArrayList<Dato> ObtenerDatos() {
        lista.clear ();
        Cursor cursor = db.rawQuery ( "select *from resultados ", null );
        if (cursor != null && cursor.getCount () > 0) {
            cursor.moveToFirst ();
            do {
                lista.add ( new Dato (
                        cursor.getString ( 0 ),
                        cursor.getString ( 7 ),
                        cursor.getString ( 2 ),
                        "",
                        cursor.getString ( 3 ),
                        "",
                        cursor.getString ( 1 ),
                        cursor.getString ( 1 ),
                        "",
                        cursor.getString ( 5 ),
                        cursor.getString ( 5 ),
                        cursor.getString ( 5 ) ) );
            } while (cursor.moveToNext ());
            return lista;
        } else {
            return null;
        }
    }


}


