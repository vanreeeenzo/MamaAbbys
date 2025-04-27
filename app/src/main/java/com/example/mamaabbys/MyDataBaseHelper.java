package com.example.mamaabbys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDataBaseHelper";
    private Context context;
    private static final String DATABASE_NAME = "MamaAbbys.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_INVENTORY = "Inventory";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "Product_Name";
    private static final String COLUMN_CATEGORY = "Product_Category";
    private static final String COLUMN_QTY = "Product_Qty";
    private static final String COLUMN_PRICE = "Product_Price";
    private static final String COLUMN_MIN_THRESHOLD = "Product_Min_Threshold";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_DELIVERY = "delivery";
    private static final String COLUMN_DELIVERY_ID = "id";
    private static final String COLUMN_ORDER = "order_description";
    private static final String COLUMN_DATE = "delivery_date";
    private static final String COLUMN_TIME = "delivery_time";
    private static final String COLUMN_STATUS = "status";

    private static final Map<String, Float> PRODUCT_PRICES = new HashMap<>();
    private static final Map<String, Integer> PRODUCT_THRESHOLDS = new HashMap<>();

    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        initializeProductPrices();
        initializeProductThresholds();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String inventoryQuery = "CREATE TABLE " + TABLE_INVENTORY + "(" + COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_QTY + " INTEGER DEFAULT 0, " +
                    COLUMN_PRICE + " FLOAT NOT NULL, " +
                    COLUMN_MIN_THRESHOLD + " INTEGER DEFAULT 10);";
            db.execSQL(inventoryQuery);

            String usersQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULLNAME + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL)";
            db.execSQL(usersQuery);

            String deliveryQuery = "CREATE TABLE " + TABLE_DELIVERY + "(" +
                    COLUMN_DELIVERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ORDER + " TEXT NOT NULL," +
                    COLUMN_DATE + " TEXT NOT NULL," +
                    COLUMN_TIME + " TEXT NOT NULL," +
                    COLUMN_STATUS + " TEXT DEFAULT 'Pending')";
            db.execSQL(deliveryQuery);

            Log.d(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage());
            throw new RuntimeException("Failed to create database tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE " + TABLE_INVENTORY + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT");
                Log.d(TAG, "Added category column to inventory table");
            }

            if (oldVersion < 3) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY);
                String deliveryQuery = "CREATE TABLE " + TABLE_DELIVERY + "(" +
                        COLUMN_DELIVERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_ORDER + " TEXT NOT NULL," +
                        COLUMN_DATE + " TEXT NOT NULL," +
                        COLUMN_TIME + " TEXT NOT NULL," +
                        COLUMN_STATUS + " TEXT DEFAULT 'Pending')";
                db.execSQL(deliveryQuery);
                Log.d(TAG, "Recreated delivery table with status column");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
            throw new RuntimeException("Failed to upgrade database", e);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
            onCreate(db);
            Log.d(TAG, "Handled database downgrade");
        } catch (Exception e) {
            Log.e(TAG, "Error downgrading database: " + e.getMessage());
        }
    }

    private void initializeProductPrices() {
        // Virginia Products
        PRODUCT_PRICES.put("Virginia Products - Virginia Classic 250 grams", 70.00f);
        PRODUCT_PRICES.put("Virginia Products - Virginia Chicken Hotdog 250 grams (Blue)", 65.00f);
        PRODUCT_PRICES.put("Virginia Products - Virginia Classic 500 grams", 125.00f);
        PRODUCT_PRICES.put("Virginia Products - Virginia Chicken Hotdog w/ Cheese (Jumbo)", 65.00f);
        PRODUCT_PRICES.put("Virginia Products - Virginia Classic 1 kilo", 210.00f);
        PRODUCT_PRICES.put("Virginia Products - Virginia w/ Cheese 1 kilo", 220.00f);
        PRODUCT_PRICES.put("Virginia Products - Chicken Longganisa", 65.00f);

        // Purefoods Products
        PRODUCT_PRICES.put("Purefoods - TJ Classic 1 kilo", 220.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Cheesedog 1 kilo", 230.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Classic 250g", 78.00f);
        PRODUCT_PRICES.put("Purefoods - Star Nuggets", 60.00f);
        PRODUCT_PRICES.put("Purefoods - Crazy Cut Nuggets", 65.00f);
        PRODUCT_PRICES.put("Purefoods - Chicken Breast Nuggets", 70.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Hotdog w/ Cheese 250 grams", 83.00f);
        PRODUCT_PRICES.put("Purefoods - TJ balls 500 grams", 130.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Chicken Jumbo", 78.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Cocktail", 83.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Cheesedog (Small)", 83.00f);
        PRODUCT_PRICES.put("Purefoods - TJ Classic (Small)", 78.00f);

        // Big Shot Products
        PRODUCT_PRICES.put("Big Shot Products - Big shot balls 500 grams", 115.00f);
        PRODUCT_PRICES.put("Big Shot Products - Big shot Classic 1 kilo", 175.00f);
        PRODUCT_PRICES.put("Big Shot Products - Big shot w/ Cheese 1 kilo", 185.00f);

        // Beefies Products
        PRODUCT_PRICES.put("Beefies Products - Beefies Classic 250 grams", 65.00f);
        PRODUCT_PRICES.put("Beefies Products - Beefies w/ Cheese 250 grams", 70.00f);
        PRODUCT_PRICES.put("Beefies Products - Beefies Classic 1 kilo", 200.00f);
        PRODUCT_PRICES.put("Beefies Products - Beefies w/ Cheese 1 kilo", 210.00f);
    }

    private void initializeProductThresholds() {
        // Virginia Products
        PRODUCT_THRESHOLDS.put("Virginia Products - Virginia Classic 250 grams", 10);
        PRODUCT_THRESHOLDS.put("Virginia Products - Virginia Chicken Hotdog 250 grams (Blue)", 10);
        PRODUCT_THRESHOLDS.put("Virginia Products - Virginia Classic 500 grams", 10);
        PRODUCT_THRESHOLDS.put("Virginia Products - Virginia Chicken Hotdog w/ Cheese (Jumbo)", 10);
        PRODUCT_THRESHOLDS.put("Virginia Products - Virginia Classic 1 kilo", 10);
        PRODUCT_THRESHOLDS.put("Virginia Products - Virginia w/ Cheese 1 kilo", 10);
        PRODUCT_THRESHOLDS.put("Virginia Products - Chicken Longganisa", 10);

        // Purefoods Products
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Classic 1 kilo", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Cheesedog 1 kilo", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Classic 250g", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - Star Nuggets", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - Crazy Cut Nuggets", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - Chicken Breast Nuggets", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Hotdog w/ Cheese 250 grams", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ balls 500 grams", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Chicken Jumbo", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Cocktail", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Cheesedog (Small)", 10);
        PRODUCT_THRESHOLDS.put("Purefoods - TJ Classic (Small)", 10);

        // Big Shot Products
        PRODUCT_THRESHOLDS.put("Big Shot Products - Big shot balls 500 grams", 10);
        PRODUCT_THRESHOLDS.put("Big Shot Products - Big shot Classic 1 kilo", 10);
        PRODUCT_THRESHOLDS.put("Big Shot Products - Big shot w/ Cheese 1 kilo", 10);

        // Beefies Products
        PRODUCT_THRESHOLDS.put("Beefies Products - Beefies Classic 250 grams", 10);
        PRODUCT_THRESHOLDS.put("Beefies Products - Beefies w/ Cheese 250 grams", 10);
        PRODUCT_THRESHOLDS.put("Beefies Products - Beefies Classic 1 kilo", 10);
        PRODUCT_THRESHOLDS.put("Beefies Products - Beefies w/ Cheese 1 kilo", 10);
    }

    void addInventory(String name, String category, int qty) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            String productKey = category + " - " + name;
            float price = PRODUCT_PRICES.getOrDefault(productKey, 100.00f);
            int min_threshold = PRODUCT_THRESHOLDS.getOrDefault(productKey, 10);

            if (isProductExists(name, category)) {
                String updateQuery = "UPDATE " + TABLE_INVENTORY + " SET " + COLUMN_QTY + " = " + COLUMN_QTY + " + ? WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_CATEGORY + " = ?";
                db.execSQL(updateQuery, new Object[]{qty, name, category});
                Toast.makeText(context, "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                cv.put(COLUMN_NAME, name);
                cv.put(COLUMN_CATEGORY, category);
                cv.put(COLUMN_QTY, qty);
                cv.put(COLUMN_PRICE, price);
                cv.put(COLUMN_MIN_THRESHOLD, min_threshold);

                long result = db.insert(TABLE_INVENTORY, null, cv);
                if (result == -1) {
                    Toast.makeText(context, "Failed to add inventory", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in addInventory: " + e.getMessage());
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All Categories");
        categories.add("Purefoods");
        categories.add("Virginia Products");
        categories.add("Big Shot Products");
        categories.add("Beefies Products");
        return categories;
    }

    public List<String> getProductsByCategory(String category) {
        List<String> products = new ArrayList<>();
        switch (category) {
            case "Purefoods":
                products.add("TJ Classic 1 kilo");
                products.add("TJ Cheesedog 1 kilo");
                products.add("TJ Classic 250g");
                products.add("Star Nuggets");
                products.add("Crazy Cut Nuggets");
                products.add("Chicken Breast Nuggets");
                products.add("TJ Hotdog w/ Cheese 250 grams");
                products.add("TJ balls 500 grams");
                products.add("TJ Chicken Jumbo");
                products.add("TJ Cocktail");
                products.add("TJ Cheesedog (Small)");
                products.add("TJ Classic (Small)");
                break;
            case "Virginia Products":
                products.add("Virginia Classic 250 grams");
                products.add("Virginia Chicken Hotdog 250 grams (Blue)");
                products.add("Virginia Classic 500 grams");
                products.add("Virginia Chicken Hotdog w/ Cheese (Jumbo)");
                products.add("Virginia Classic 1 kilo");
                products.add("Virginia w/ Cheese 1 kilo");
                products.add("Chicken Longganisa");
                break;
            case "Big Shot Products":
                products.add("Big shot balls 500 grams");
                products.add("Big shot Classic 1 kilo");
                products.add("Big shot w/ Cheese 1 kilo");
                break;
            case "Beefies Products":
                products.add("Beefies Classic 250 grams");
                products.add("Beefies w/ Cheese 250 grams");
                products.add("Beefies Classic 1 kilo");
                products.add("Beefies w/ Cheese 1 kilo");
                break;
        }
        return products;
    }

    public boolean isProductExists(String productName, String category) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_INVENTORY + " WHERE " + COLUMN_NAME + " = ? AND " + COLUMN_CATEGORY + " = ?";
            cursor = db.rawQuery(query, new String[]{productName, category});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error in isProductExists: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> inventoryItems = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_CATEGORY + ", " +
                    COLUMN_QTY + ", " + COLUMN_PRICE + ", " + COLUMN_MIN_THRESHOLD + " FROM " + TABLE_INVENTORY;
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String name = cursor.getString(1);
                    String category = cursor.getString(2);
                    int quantity = cursor.getInt(3);
                    float price = cursor.getFloat(4);
                    int minThreshold = cursor.getInt(5);
                    String stockInfo = "In Stock: " + quantity + " | Price: ₱" + String.format("%.2f", price);

                    if (quantity <= minThreshold) {
                        stockInfo += " (Low Stock!)";
                    }

                    inventoryItems.add(new InventoryItem(id, name, stockInfo, R.drawable.ic_package, category));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getAllInventoryItems: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return inventoryItems;
    }

    public void deleteAllInventory() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_INVENTORY, null, null);
            Toast.makeText(context, "All inventory items deleted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all inventory: " + e.getMessage());
            Toast.makeText(context, "Error deleting inventory: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, user.getFullname());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public boolean checkUser(String fullname, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_FULLNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{fullname, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    public List<InventoryItem> searchInventoryItems(String query) {
        List<InventoryItem> inventoryItems = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String searchQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_CATEGORY + ", " +
                    COLUMN_QTY + ", " + COLUMN_PRICE + ", " + COLUMN_MIN_THRESHOLD + " FROM " + TABLE_INVENTORY +
                    " WHERE " + COLUMN_NAME + " LIKE ? OR " + COLUMN_CATEGORY + " LIKE ?";
            String searchPattern = "%" + query + "%";
            cursor = db.rawQuery(searchQuery, new String[]{searchPattern, searchPattern});

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String name = cursor.getString(1);
                    String category = cursor.getString(2);
                    int quantity = cursor.getInt(3);
                    float price = cursor.getFloat(4);
                    int minThreshold = cursor.getInt(5);
                    String stockInfo = "In Stock: " + quantity + " | Price: ₱" + String.format("%.2f", price);

                    if (quantity <= minThreshold) {
                        stockInfo += " (Low Stock!)";
                    }

                    inventoryItems.add(new InventoryItem(id, name, stockInfo, R.drawable.ic_package, category));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in searchInventoryItems: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return inventoryItems;
    }

    public boolean addDelivery(Delivery delivery) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ORDER, delivery.getOrderDescription());
            values.put(COLUMN_DATE, delivery.getDeliveryDate());
            values.put(COLUMN_TIME, delivery.getDeliveryTime());
            values.put(COLUMN_STATUS, "Pending");

            long result = db.insert(TABLE_DELIVERY, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding delivery: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_DELIVERY +
                    " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_TIME + " DESC";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ID));
                    String order = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER));
                    String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                    String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                    Delivery delivery = new Delivery(order, date, time);
                    delivery.setId(id);
                    delivery.setStatus(status);
                    deliveries.add(delivery);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return deliveries;
    }


    public boolean deleteDelivery(String deliveryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_DELIVERY, COLUMN_DELIVERY_ID + " = ?", new String[]{deliveryId});
            return result > 0;
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean updateDeliveryStatus(String deliveryId, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, isDone ? "Done" : "Pending");

        try {
            int rowsAffected = db.update(TABLE_DELIVERY, values, COLUMN_DELIVERY_ID + " = ?", new String[]{deliveryId});
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }





}



