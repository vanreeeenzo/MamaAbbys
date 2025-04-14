package com.example.mamaabbys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "MamaAbbys.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Inventory";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "Product_Name";
    private static final String COLUMN_QTY = "Product_Qty";
    private static final String COLUMN_PRICE = "Product_Price";
    private static final String COLUMN_MIN_THRESHOLD = "Product_Min_Threshold";

    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_QTY + " INTEGER, " +
                COLUMN_PRICE + " FLOAT, " +
                COLUMN_MIN_THRESHOLD + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addInventory(String name, int qty, float price, int min_threshold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_QTY, qty);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_MIN_THRESHOLD, min_threshold);
        
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Failed to add inventory", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> getAllProductNames() {
        List<String> productNames = new ArrayList<>();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                productNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productNames;
    }

    public boolean isProductExists(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{productName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void updateProduct(String name, int qty, float price, int min_threshold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        cv.put(COLUMN_QTY, qty);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_MIN_THRESHOLD, min_threshold);
        
        long result = db.update(TABLE_NAME, cv, COLUMN_NAME + "=?", new String[]{name});
        if(result == -1) {
            Toast.makeText(context, "Failed to update inventory", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
