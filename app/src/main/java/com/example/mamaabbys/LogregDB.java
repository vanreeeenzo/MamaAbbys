package com.example.mamaabbys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import androidx.annotation.Nullable;

public class LogregDB extends SQLiteOpenHelper {

    private static final String dbName = "logreg";
    private static final String dbTable = "users";
    private static final int dbVer = 1;

    private static final String id = "id";
    private static final String fullname = "fullname";
    private static final String password = "password";
    private static final String email = "email";




    public LogregDB(@Nullable Context context) {
        super(context, dbName, null, dbVer);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + dbTable + " (" +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                fullname + " TEXT, " +
                email + " TEXT, " +
                password + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + dbTable);
        onCreate(db);
    }

    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(fullname, user.getFullname());
        values.put(email, user.getEmail());
        values.put(password, user.getPassword());
        db.insert(dbTable, null, values);

    }


    public boolean checkUser(String fullname, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbTable + " WHERE fullname = ? AND password = ?", new String[]{fullname, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }
}
