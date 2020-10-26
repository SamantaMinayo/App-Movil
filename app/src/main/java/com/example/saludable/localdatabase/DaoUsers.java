package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.example.saludable.Model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DaoUsers {

    SQLiteDatabase db;
    User usr;
    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists user(" +
            "uid text primary key,email text,altura text, edad text, peso text, genero text," +
            "fullname text,username text, country text, imc text, profileimage text, status text" +
            ",rango text, paso text)";

    public DaoUsers(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(User user) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", user.getUid () );
        contenedor.put ( "email", user.getEmail () );
        contenedor.put ( "altura", user.getAltura () );
        contenedor.put ( "edad", user.getEdad () );
        contenedor.put ( "peso", user.getPeso () );
        contenedor.put ( "genero", user.getGenero () );
        contenedor.put ( "fullname", user.getFullname () );
        contenedor.put ( "username", user.getUsername () );
        contenedor.put ( "country", user.getCountry () );
        contenedor.put ( "imc", user.getImc () );
        contenedor.put ( "profileimage", user.getProfileimage () );
        contenedor.put ( "status", user.getStatus () );
        contenedor.put ( "rango", user.getRango () );
        contenedor.put ( "paso", user.getPaso () );
        return (db.insert ( "user", null, contenedor )) > 0;
    }

    public boolean Eliminar(String uid) {
        return (db.delete ( "user", "uid=" + "'" + uid + "'", null )) > 0;
    }

    public boolean Editar(User usr) {
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "uid", usr.getUid () );
        contenedor.put ( "email", usr.getEmail () );
        contenedor.put ( "altura", usr.getAltura () );
        contenedor.put ( "edad", usr.getEdad () );
        contenedor.put ( "peso", usr.getPeso () );
        contenedor.put ( "genero", usr.getGenero () );
        contenedor.put ( "fullname", usr.getFullname () );
        contenedor.put ( "username", usr.getUsername () );
        contenedor.put ( "country", usr.getCountry () );
        contenedor.put ( "imc", usr.getImc () );
        contenedor.put ( "profileimage", usr.getProfileimage () );
        contenedor.put ( "status", usr.getStatus () );
        contenedor.put ( "rango", usr.getRango () );
        contenedor.put ( "paso", usr.getPaso () );
        return (db.update ( "user", contenedor, "uid=" + "'" + usr.getUid () + "'", null )) > 0;
    }

    public User ObtenerUsuario() {
        Cursor cursor = db.rawQuery ( "select *from user", null );
        if (cursor.getCount () > 0) {
            cursor.moveToFirst ();
            usr = new User ( cursor.getString ( 0 ),
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
                    cursor.getString ( 11 ),
                    cursor.getString ( 12 ),
                    cursor.getString ( 13 )
            );
            return usr;
        } else {
            return null;
        }

    }

    public boolean InsertImagen(String uid, Bitmap bitmap, Context ctx) throws IOException {

        File dir = new File ( Environment.getExternalStorageDirectory () + "/.MiCarpeta/" );
        if (!dir.exists ()) {
            System.out.println ( "creando directorio: " + "MiCarpeta" );
            dir.mkdir ();
        }
        File file = new File ( dir, uid + ".jpg" );
        if (file.exists ()) {
            file.delete ();
        }
        FileOutputStream out = new FileOutputStream ( file );
        bitmap.compress ( Bitmap.CompressFormat.JPEG, 90, out );
        out.flush ();
        out.close ();
        String path = file.getAbsolutePath ();
        ContentValues contenedor = new ContentValues ();
        contenedor.put ( "profileimage", path );
        return (db.update ( "user", contenedor, "uid=" + "'" + uid + "'", null )) > 0;
    }

}
