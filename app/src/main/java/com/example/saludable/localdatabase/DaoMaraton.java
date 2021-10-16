package com.example.saludable.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.example.saludable.Model.Maraton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DaoMaraton {

    SQLiteDatabase db;
    Maraton maraton;
    Context ctx;
    String nombredb = "SaludableDB";
    String tabla = " create table if not exists maraton(" +
            "uid text primary key,time text,date text,maratonimage text,description text," +
            "contactname text,contactnumber text,maratontime text,maratondate text,place text," +
            "maratonname text,estado text,codigo text,maratondist text," +
            "maratontrayectoriaweb text,image text)";

    public DaoMaraton(Context c) {
        this.ctx = c;
        db = c.openOrCreateDatabase ( nombredb, Context.MODE_PRIVATE, null );
        db.execSQL ( tabla );
    }

    public boolean InsertEditar(Maraton mara) {
        if (ObtenerMaraton ( mara.getUid () ) == null) {
            ContentValues contenedor = new ContentValues ();
            contenedor.put ( "uid", mara.getUid () );
            contenedor.put ( "time", mara.getTime () );
            contenedor.put ( "date", mara.getDate () );
            contenedor.put ( "description", mara.getDescription () );
            contenedor.put ( "contactname", mara.getContactname () );
            contenedor.put ( "contactnumber", mara.getContactnumber () );
            contenedor.put ( "maratontime", mara.getMaratontime () );
            contenedor.put ( "maratondate", mara.getMaratondate () );
            contenedor.put ( "place", mara.getPlace () );
            contenedor.put ( "maratonname", mara.getMaratonname () );
            contenedor.put ( "estado", mara.getEstado () );
            contenedor.put ( "codigo", mara.getCodigo () );
            contenedor.put ( "maratondist", mara.getMaratondist () );
            contenedor.put ( "maratontrayectoriaweb", mara.getMaratontrayectoriaweb () );
            contenedor.put ( "maratonimage", mara.getMaratonimage () );
            return (db.insert ( "maraton", null, contenedor )) > 0;
        } else {
            ContentValues contenedor = new ContentValues ();
            if (!mara.getUid ().isEmpty ()) {
                contenedor.put ( "uid", mara.getUid () );
            }
            if (!mara.getTime ().isEmpty ()) {
                contenedor.put ( "time", mara.getTime () );
            }
            if (!mara.getDate ().isEmpty ()) {
                contenedor.put ( "date", mara.getDate () );
            }
            if (!mara.getDescription ().isEmpty ()) {
                contenedor.put ( "description", mara.getDescription () );
            }
            if (!mara.getContactname ().isEmpty ()) {
                contenedor.put ( "contactname", mara.getContactname () );
            }
            if (!mara.getContactnumber ().isEmpty ()) {
                contenedor.put ( "contactnumber", mara.getContactnumber () );
            }
            if (!mara.getMaratontime ().isEmpty ()) {
                contenedor.put ( "maratontime", mara.getMaratontime () );
            }
            if (!mara.getMaratondate ().isEmpty ()) {
                contenedor.put ( "maratondate", mara.getMaratondate () );
            }
            if (!mara.getPlace ().isEmpty ()) {
                contenedor.put ( "place", mara.getPlace () );
            }
            if (!mara.getMaratonname ().isEmpty ()) {
                contenedor.put ( "maratonname", mara.getMaratonname () );
            }
            if (!mara.getEstado ().isEmpty ()) {
                contenedor.put ( "estado", mara.getEstado () );
            }
            if (!mara.getCodigo ().isEmpty ()) {
                contenedor.put ( "codigo", mara.getCodigo () );
            }

            if (!mara.getMaratondist ().isEmpty ()) {
                contenedor.put ( "maratondist", mara.getMaratondist () );
            }

            if (!mara.getMaratontrayectoriaweb ().isEmpty ()) {
                contenedor.put ( "maratontrayectoriaweb", mara.getMaratontrayectoriaweb () );
            }

            if (!mara.getImage ().isEmpty ()) {
                contenedor.put ( "maratonimage", mara.getMaratonimage () );
            }
            return (db.update ( "maraton", contenedor, "uid=" + "'" + maraton.getUid () + "'", null )) > 0;
        }

    }

    public boolean Eliminar(String uid) {
        return (db.delete ( "maraton", "uid=" + "'" + uid + "'", null )) > 0;
    }

    public Maraton ObtenerMaraton(String id) {
        Cursor cursor = db.rawQuery ( "select *from maraton where uid=" + "'" + id + "'", null );
        cursor.moveToFirst ();
        if (cursor.getCount () > 0) {
            maraton = new Maraton ( cursor.getString ( 0 ),
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
                    cursor.getString ( 12 ),
                    cursor.getString ( 11 ),
                    cursor.getString ( 13 ),
                    cursor.getString ( 14 ),
                    cursor.getString ( 15 )
            );
            return maraton;
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
        contenedor.put ( "image", path );
        contenedor.put ( "uid", uid );
        if (ObtenerMaraton ( uid ) != null) {
            return true;
            //     return (db.update ( "maraton", contenedor, "uid=" + "'" + uid + "'", null )) > 0;
        } else {
            return true;

            //   return (db.insert ( "maraton", null, contenedor )) > 0;
        }
    }
}
