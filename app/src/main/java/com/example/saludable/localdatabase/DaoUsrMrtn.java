package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.saludable.Model.Maraton;
import com.example.saludable.Model.UsrMrtn;

import java.util.ArrayList;

public class DaoUsrMrtn {

    public SQLiteDatabase db;
    UsrMrtn usr;
    ArrayList<Maraton> lista = new ArrayList<Maraton> ();
    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists usermrtn(" +
            "id Integer primary key autoincrement, uid text,estado text)";

    public DaoUsrMrtn(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(UsrMrtn usrMrtn) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", usrMrtn.getUid () );
        contenedor.put ( "estado", usrMrtn.getEstado () );
        return (db.insert ( "usermrtn", null, contenedor )) > 0;
    }

    public boolean Eliminar(String uid) {
        return (db.delete ( "usermrtn", "uid=" + "'" + uid + "'", null )) > 0;
    }

    public boolean Editar(UsrMrtn usrMrtn) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "estado", usrMrtn.getEstado () );
        return (db.update ( "usermrtn", contenedor, "uid=" + "'" + usr.getUid () + "'", null )) > 0;
    }

    public UsrMrtn Obtener(String id) {
        Cursor cursor = db.rawQuery ( "select *from usermrtn where uid=" + "'" + id + "'", null );
        cursor.moveToFirst ();
        if (cursor.getCount () > 0) {
            usr = new UsrMrtn ( cursor.getInt ( 0 ),
                    cursor.getString ( 1 ),
                    cursor.getString ( 2 )
            );
            return usr;
        } else {
            return null;
        }
    }

    public ArrayList<Maraton> ObtenerMaratonList(String estado) {
        lista.clear ();
        Cursor cursor = db.rawQuery ( "select * from usermrtn WHERE estado=" + "'" + estado + "'", null );
        if (cursor != null && cursor.getCount () > 0) {
            cursor.moveToFirst ();
            do {
                Cursor nuevo = db.rawQuery ( "select * from maraton WHERE uid=" + "'" + cursor.getString ( 1 ) + "'", null );
                if (nuevo != null && nuevo.getCount () > 0) {
                    nuevo.moveToFirst ();
                    do {
                        lista.add ( new Maraton ( nuevo.getString ( 0 ),
                                nuevo.getString ( 1 ),
                                nuevo.getString ( 2 ),
                                nuevo.getString ( 3 ),
                                nuevo.getString ( 4 ),
                                nuevo.getString ( 5 ),
                                nuevo.getString ( 6 ),
                                nuevo.getString ( 7 ),
                                nuevo.getString ( 8 ),
                                nuevo.getString ( 9 ),
                                nuevo.getString ( 10 ),
                                nuevo.getString ( 11 ),
                                nuevo.getString ( 12 ),
                                nuevo.getString ( 13 ),
                                nuevo.getString ( 14 )
                        ) );
                    } while (nuevo.moveToNext ());
                } else {
                    return null;
                }
            } while (cursor.moveToNext ());
            return lista;
        } else {
            return null;
        }
    }

}
