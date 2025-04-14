package com.example.mamaabbys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "MamaAbbys.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Inventory";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "Product_Name";
    private static final String COLUMN_PRICE = "Product_Price";
    private static final String COLUMN_STOCK = "Product_Stock";
    private static final String COLUMN_MIN_THRESHOLD = "Product_Min_Threshold";
    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_STOCK + " TEXT, " +
                COLUMN_MIN_THRESHOLD + " FLOAT);";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
