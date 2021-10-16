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
            ",rango text, paso text, image text)";

    public DaoUsers(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean Insert(User user) {
        if (ObtenerUsuario () == null) {
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
            contenedor.put ( "image", user.getImage () );

            return (db.insert ( "user", null, contenedor )) > 0;
        } else {
            ContentValues contenedor = new ContentValues ();
            if (!user.getUid ().isEmpty ()) {
                contenedor.put ( "uid", user.getUid () );
            }
            if (!user.getEmail ().isEmpty ()) {
                contenedor.put ( "email", user.getEmail () );
            }
            if (!user.getAltura ().isEmpty ()) {
                contenedor.put ( "altura", user.getAltura () );
            }
            if (!user.getEdad ().isEmpty ()) {
                contenedor.put ( "edad", user.getEdad () );
            }
            if (!user.getPeso ().isEmpty ()) {
                contenedor.put ( "peso", user.getPeso () );
            }
            if (!user.getGenero ().isEmpty ()) {
                contenedor.put ( "genero", user.getGenero () );
            }
            if (!user.getFullname ().isEmpty ()) {
                contenedor.put ( "fullname", user.getFullname () );
            }
            if (!user.getUsername ().isEmpty ()) {
                contenedor.put ( "username", user.getUsername () );
            }
            if (!user.getCountry ().isEmpty ()) {
                contenedor.put ( "country", user.getCountry () );
            }
            if (!user.getImc ().isEmpty ()) {
                contenedor.put ( "imc", user.getImc () );
            }
            if (!user.getProfileimage ().isEmpty ()) {
                contenedor.put ( "profileimage", user.getProfileimage () );
            }
            if (!user.getStatus ().isEmpty ()) {
                contenedor.put ( "status", user.getStatus () );
            }
            if (!user.getRango ().isEmpty ()) {
                contenedor.put ( "rango", user.getRango () );
            }
            if (!user.getPaso ().isEmpty ()) {
                contenedor.put ( "paso", user.getPaso () );
            }
            if (!user.getImage ().isEmpty ()) {
                contenedor.put ( "image", user.getImage () );
            }
            return (db.update ( "user", contenedor, "uid=" + "'" + usr.getUid () + "'", null )) > 0;
        }
    }

    public boolean Eliminar(String uid) {
        return (db.delete ( "user", "uid=" + "'" + uid + "'", null )) > 0;
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
                    cursor.getString ( 13 ),
                    cursor.getString ( 14 )
            );
            return usr;
        } else {
            return null;
        }

    }

    public boolean InsertImagen(String uid, Bitmap bitmap, Context ctx) throws IOException {
        boolean success = true;
        File dir = new File ( Environment.getExternalStorageDirectory () + "/.MiCarpeta/" );
        if (!dir.exists ()) {
            System.out.println ( "creando directorio: " + "MiCarpeta" );
            success = dir.mkdirs ();
        }
        if (success) {
            System.out.println ( "Se creo: " + "MiCarpeta" );
            // Do something on success
        } else {
            System.out.println ( "No se creo: " + "MiCarpeta" );
// Do something else on failure
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
        contenedor.put ( "image", path );
        return (db.update ( "user", contenedor, "uid=" + "'" + uid + "'", null )) > 0;
    }

}
