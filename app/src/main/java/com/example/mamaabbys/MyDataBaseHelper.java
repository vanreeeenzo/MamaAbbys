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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDataBaseHelper";
    private Context context;
    private static final String DATABASE_NAME = "MamaAbbys.db";
    private static final int DATABASE_VERSION = 7;

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
    private static final String COLUMN_LOCATION = "location";

    private static final String TABLE_DELETED_NOTIFICATIONS = "deleted_notifications";
    private static final String TABLE_READ_NOTIFICATIONS = "read_notifications";
    private static final String COLUMN_NOTIFICATION_ID = "notification_id";
    private static final String COLUMN_DELETED_AT = "deleted_at";
    private static final String COLUMN_READ_AT = "read_at";

    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_ID = "id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_SALE_DATE = "sale_date";

    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "id";
    private static final String COLUMN_ORDER_DATE = "order_date";
    private static final String COLUMN_ORDER_TIME = "order_time";
    private static final String COLUMN_ORDER_TOTAL = "order_total";
    private static final String COLUMN_ORDER_STATUS = "order_status";

    private static final String TABLE_ORDER_ITEMS = "order_items";
    private static final String COLUMN_ORDER_ITEM_ID = "id";
    private static final String COLUMN_ORDER_ID_FK = "order_id";
    private static final String COLUMN_ORDER_PRODUCT_ID = "product_id";
    private static final String COLUMN_ORDER_QUANTITY = "quantity";
    private static final String COLUMN_ORDER_ITEM_PRICE = "price";
    private static final String COLUMN_ORDER_ITEM_TOTAL = "total";

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
            Log.d(TAG, "Inventory table created successfully");

            String ordersQuery = "CREATE TABLE " + TABLE_ORDERS + "(" +
                    COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                    COLUMN_ORDER_TIME + " TEXT NOT NULL, " +
                    COLUMN_ORDER_TOTAL + " FLOAT NOT NULL, " +
                    COLUMN_ORDER_STATUS + " TEXT DEFAULT 'Completed');";
            db.execSQL(ordersQuery);
            Log.d(TAG, "Orders table created successfully");

            String orderItemsQuery = "CREATE TABLE " + TABLE_ORDER_ITEMS + "(" +
                    COLUMN_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_ID_FK + " INTEGER NOT NULL, " +
                    COLUMN_ORDER_PRODUCT_ID + " INTEGER NOT NULL, " +
                    COLUMN_ORDER_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_ORDER_ITEM_PRICE + " FLOAT NOT NULL, " +
                    COLUMN_ORDER_ITEM_TOTAL + " FLOAT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_ORDER_ID_FK + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_ORDER_PRODUCT_ID + ") REFERENCES " + TABLE_INVENTORY + "(" + COLUMN_ID + "));";
            db.execSQL(orderItemsQuery);
            Log.d(TAG, "Order items table created successfully");

            String usersQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULLNAME + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL)";
            db.execSQL(usersQuery);
            Log.d(TAG, "Users table created successfully");

            String createDeliveryTable = "CREATE TABLE " + TABLE_DELIVERY + " (" +
                    COLUMN_DELIVERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_STATUS + " TEXT, " +
                    COLUMN_LOCATION + " TEXT" +
                    ")";
            db.execSQL(createDeliveryTable);
            Log.d(TAG, "Delivery table created successfully");

            String createDeletedNotificationsTable = "CREATE TABLE " + TABLE_DELETED_NOTIFICATIONS + " (" +
                    COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_DELIVERY_ID + " TEXT NOT NULL," +
                    COLUMN_DELETED_AT + " INTEGER NOT NULL)";
            db.execSQL(createDeletedNotificationsTable);
            Log.d(TAG, "Deleted notifications table created successfully");

            String createReadNotificationsTable = "CREATE TABLE " + TABLE_READ_NOTIFICATIONS + " (" +
                    COLUMN_NOTIFICATION_ID + " TEXT PRIMARY KEY," +
                    COLUMN_READ_AT + " INTEGER NOT NULL)";
            db.execSQL(createReadNotificationsTable);
            Log.d(TAG, "Read notifications table created successfully");

            Log.d(TAG, "All database tables created successfully");
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

            if (oldVersion < 4) {
                String createDeletedNotificationsTable = "CREATE TABLE " + TABLE_DELETED_NOTIFICATIONS + " (" +
                        COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_DELIVERY_ID + " TEXT NOT NULL," +
                        COLUMN_DELETED_AT + " INTEGER NOT NULL)";
                db.execSQL(createDeletedNotificationsTable);
                Log.d(TAG, "Created deleted notifications table");
            }

            if (oldVersion < 7) {
                db.execSQL("ALTER TABLE " + TABLE_DELIVERY + " ADD COLUMN " + COLUMN_LOCATION + " TEXT");
                Log.d(TAG, "Added location column to delivery table");
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

                    if (quantity <= 0) {
                        stockInfo += " (Out of Stock!)";
                    } else if (quantity <= minThreshold) {
                        stockInfo += " (Low Stock!)";
                    }

                    InventoryItem item = new InventoryItem(id, name, stockInfo, R.drawable.ic_package, category);
                    item.setQuantity(quantity);
                    item.setPrice(price);
                    item.setMinThreshold(minThreshold);
                    inventoryItems.add(item);
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

                    if (quantity <= 0) {
                        stockInfo += " (Out of Stock!)";
                    } else if (quantity <= minThreshold) {
                        stockInfo += " (Low Stock!)";
                    }

                    InventoryItem item = new InventoryItem(id, name, stockInfo, R.drawable.ic_package, category);
                    item.setQuantity(quantity);
                    item.setPrice(price);
                    item.setMinThreshold(minThreshold);
                    inventoryItems.add(item);
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
            values.put(COLUMN_LOCATION, delivery.getLocation());

            long result = db.insert(TABLE_DELIVERY, null, values);
            if (result == -1) {
                Log.e(TAG, "Failed to insert delivery into database");
                return false;
            }
            Log.d(TAG, "Successfully added delivery with ID: " + result);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error adding delivery: " + e.getMessage());
            e.printStackTrace();
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
                    String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));

                    Delivery delivery = new Delivery(order, date, time, location);
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

    public boolean markNotificationAsDeleted(String deliveryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DELIVERY_ID, deliveryId);
            values.put(COLUMN_DELETED_AT, System.currentTimeMillis());

            long result = db.insert(TABLE_DELETED_NOTIFICATIONS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error marking notification as deleted: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean isNotificationDeleted(String deliveryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_DELETED_NOTIFICATIONS +
                    " WHERE " + COLUMN_DELIVERY_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{deliveryId});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking deleted notification: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public Delivery getDeliveryById(String deliveryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_DELIVERY + " WHERE " + COLUMN_DELIVERY_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{deliveryId});
            
            if (cursor != null && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELIVERY_ID));
                String orderDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER));
                String deliveryDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String deliveryTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                
                Delivery delivery = new Delivery(orderDescription, deliveryDate, deliveryTime, location);
                delivery.setId(id);
                return delivery;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting delivery by ID: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean markNotificationAsRead(String notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTIFICATION_ID, notificationId);
            values.put(COLUMN_READ_AT, System.currentTimeMillis());

            long result = db.insertWithOnConflict(TABLE_READ_NOTIFICATIONS, null, values, 
                    SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error marking notification as read: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean isNotificationRead(String notificationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_READ_NOTIFICATIONS +
                    " WHERE " + COLUMN_NOTIFICATION_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{notificationId});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking read notification: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public String sellInventoryItem(String itemId, int quantity) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            cursor = db.query(TABLE_INVENTORY,
                    new String[]{COLUMN_QTY, COLUMN_PRICE, COLUMN_NAME},
                    COLUMN_ID + " = ?",
                    new String[]{itemId},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int currentQty = cursor.getInt(0);
                float price = cursor.getFloat(1);
                String productName = cursor.getString(2);

                if (currentQty >= quantity) {
                    int newQty = currentQty - quantity;
                    float totalAmount = price * quantity;

                    db.beginTransaction();
                    try {
                        // Update inventory quantity
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(COLUMN_QTY, newQty);
                        db.update(TABLE_INVENTORY, updateValues, COLUMN_ID + " = ?", new String[]{itemId});

                        // Create new order
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                        ContentValues orderValues = new ContentValues();
                        orderValues.put(COLUMN_ORDER_DATE, currentDate);
                        orderValues.put(COLUMN_ORDER_TIME, currentTime);
                        orderValues.put(COLUMN_ORDER_TOTAL, totalAmount);
                        orderValues.put(COLUMN_ORDER_STATUS, "Completed");

                        long orderId = db.insert(TABLE_ORDERS, null, orderValues);

                        if (orderId != -1) {
                            // Add order item
                            ContentValues orderItemValues = new ContentValues();
                            orderItemValues.put(COLUMN_ORDER_ID_FK, orderId);
                            orderItemValues.put(COLUMN_ORDER_PRODUCT_ID, itemId);
                            orderItemValues.put(COLUMN_ORDER_QUANTITY, quantity);
                            orderItemValues.put(COLUMN_ORDER_ITEM_PRICE, price);
                            orderItemValues.put(COLUMN_ORDER_ITEM_TOTAL, totalAmount);

                            long orderItemId = db.insert(TABLE_ORDER_ITEMS, null, orderItemValues);

                            if (orderItemId != -1) {
                                db.setTransactionSuccessful();
                                return String.format("Sold %d %s for ₱%.2f", quantity, productName, totalAmount);
                            }
                        }
                        return "Failed to record sale";
                    } catch (Exception e) {
                        return "Error recording sale: " + e.getMessage();
                    } finally {
                        db.endTransaction();
                    }
                } else {
                    return "Not enough stock available!";
                }
            } else {
                return "Product not found!";
            }
        } catch (Exception e) {
            return "Error recording sale: " + e.getMessage();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            
            // Always sort by date and time in descending order (newest first)
            String orderBy = COLUMN_ORDER_DATE + " DESC, " + COLUMN_ORDER_TIME + " DESC";
            
            cursor = db.query(TABLE_ORDERS,
                    new String[]{COLUMN_ORDER_ID, COLUMN_ORDER_DATE, COLUMN_ORDER_TIME, COLUMN_ORDER_TOTAL, COLUMN_ORDER_STATUS},
                    null, null, null, null, orderBy);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String date = cursor.getString(1);
                    String time = cursor.getString(2);
                    double total = cursor.getDouble(3);
                    String status = cursor.getString(4);
                    
                    Order order = new Order(id, date, time, total, status);
                    
                    // Get order items
                    Cursor itemsCursor = db.query(TABLE_ORDER_ITEMS,
                            new String[]{COLUMN_ORDER_PRODUCT_ID, COLUMN_ORDER_QUANTITY, COLUMN_ORDER_ITEM_PRICE, COLUMN_ORDER_ITEM_TOTAL},
                            COLUMN_ORDER_ID_FK + " = ?",
                            new String[]{id},
                            null, null, null);
                    
                    if (itemsCursor != null && itemsCursor.moveToFirst()) {
                        do {
                            String productId = itemsCursor.getString(0);
                            int quantity = itemsCursor.getInt(1);
                            double price = itemsCursor.getDouble(2);
                            double itemTotal = itemsCursor.getDouble(3);
                            
                            // Get product name
                            Cursor productCursor = db.query(TABLE_INVENTORY,
                                    new String[]{COLUMN_NAME},
                                    COLUMN_ID + " = ?",
                                    new String[]{productId},
                                    null, null, null);
                            
                            String productName = "";
                            if (productCursor != null && productCursor.moveToFirst()) {
                                productName = productCursor.getString(0);
                            }
                            if (productCursor != null) {
                                productCursor.close();
                            }
                            
                            order.addItem(new Order.OrderItem(productId, productName, quantity, price, itemTotal));
                        } while (itemsCursor.moveToNext());
                    }
                    if (itemsCursor != null) {
                        itemsCursor.close();
                    }
                    
                    orders.add(order);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting orders: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return orders;
    }

    public void deleteAllOrders() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            db.delete(TABLE_ORDER_ITEMS, null, null);
            db.delete(TABLE_ORDERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all orders: " + e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
                if (db.isOpen()) {
                    db.close();
                }
            }
        }
    }

    public SalesSummary getSalesSummary() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        SalesSummary summary = new SalesSummary();
        
        try {
            db = this.getReadableDatabase();
            
            // Get total revenue and total orders in a single query
            String query = "SELECT COUNT(*) as total_orders, " +
                         "COALESCE(SUM(" + COLUMN_TOTAL_AMOUNT + "), 0) as total_revenue " +
                         "FROM " + TABLE_SALES;
            
            cursor = db.rawQuery(query, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                int totalOrders = cursor.getInt(0);
                double totalRevenue = cursor.getDouble(1);
                
                summary.setTotalOrders(totalOrders);
                summary.setTotalRevenue(totalRevenue);
                
                // Average order value will be calculated automatically in SalesSummary
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting sales summary: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return summary;
    }

    public List<SalesRecord> getRecentSales() {
        List<SalesRecord> salesRecords = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            
            // Join sales table with inventory table to get product names
            String query = "SELECT s." + COLUMN_SALE_ID + ", s." + COLUMN_PRODUCT_ID + 
                    ", i." + COLUMN_NAME + ", s." + COLUMN_QUANTITY + 
                    ", s." + COLUMN_TOTAL_AMOUNT + ", s." + COLUMN_SALE_DATE +
                    " FROM " + TABLE_SALES + " s" +
                    " LEFT JOIN " + TABLE_INVENTORY + " i ON s." + COLUMN_PRODUCT_ID + 
                    " = i." + COLUMN_ID +
                    " ORDER BY s." + COLUMN_SALE_DATE + " DESC";
            
            cursor = db.rawQuery(query, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String productId = cursor.getString(1);
                    String productName = cursor.getString(2);
                    int quantity = cursor.getInt(3);
                    double totalAmount = cursor.getDouble(4);
                    String saleDate = cursor.getString(5);
                    
                    salesRecords.add(new SalesRecord(id, productId, productName, quantity, totalAmount, saleDate));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting recent sales: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return salesRecords;
    }

    public void deleteAllSales() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_SALES, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all sales: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<InventoryItem> getOutOfStockItems() {
        List<InventoryItem> outOfStockItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_INVENTORY + " WHERE " + COLUMN_QTY + " = 0";
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QTY));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                int minThreshold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MIN_THRESHOLD));
                
                String stockInfo = String.format("In Stock: %d | Price: ₱%.2f (Out of Stock!)", quantity, price);
                
                InventoryItem item = new InventoryItem(id, name, stockInfo, R.drawable.ic_package, category);
                item.setQuantity(quantity);
                item.setPrice(price);
                item.setMinThreshold(minThreshold);
                
                outOfStockItems.add(item);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return outOfStockItems;
    }

    public double getTodayRevenue() {
        return getRevenueByDate(getTodayDate());
    }

    public double getWeeklyRevenue() {
        return getRevenueByDateRange(getWeekStartDate(), getTodayDate());
    }

    public double getMonthlyRevenue() {
        return getRevenueByDateRange(getMonthStartDate(), getTodayDate());
    }

    private double getRevenueByDate(String date) {
        double revenue = 0.0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            String query = "SELECT COALESCE(SUM(" + COLUMN_ORDER_TOTAL + "), 0) as total" +
                         " FROM " + TABLE_ORDERS +
                         " WHERE date(" + COLUMN_ORDER_DATE + ") = date(?)";
            
            cursor = db.rawQuery(query, new String[]{date});
            
            if (cursor != null && cursor.moveToFirst()) {
                revenue = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting revenue by date: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return revenue;
    }

    private double getRevenueByDateRange(String startDate, String endDate) {
        double revenue = 0.0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            String query = "SELECT COALESCE(SUM(" + COLUMN_ORDER_TOTAL + "), 0) as total" +
                         " FROM " + TABLE_ORDERS +
                         " WHERE date(" + COLUMN_ORDER_DATE + ") BETWEEN date(?) AND date(?)";
            
            cursor = db.rawQuery(query, new String[]{startDate, endDate});
            
            if (cursor != null && cursor.moveToFirst()) {
                revenue = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting revenue by date range: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return revenue;
    }

    private String getTodayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private String getWeekStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private String getMonthStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public boolean updateProductPrice(String productId, float newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRICE, newPrice);

            int rowsAffected = db.update(TABLE_INVENTORY, values, 
                COLUMN_ID + " = ?", new String[]{productId});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating product price: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_INVENTORY, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                    float price = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRICE));

                    products.add(new Product(id, name, category, price));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return products;
    }

    public float getProductPrice(String fullProductName) {
        // First try to get from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] parts = fullProductName.split(" - ", 2);
            if (parts.length == 2) {
                String category = parts[0];
                String name = parts[1];
                cursor = db.query(TABLE_INVENTORY,
                        new String[]{COLUMN_PRICE},
                        COLUMN_NAME + " = ? AND " + COLUMN_CATEGORY + " = ?",
                        new String[]{name, category},
                        null, null, null);
                
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getFloat(0);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting product price from database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // Fallback to predefined price if not found in database
        return PRODUCT_PRICES.getOrDefault(fullProductName, 0.0f);
    }

    public boolean updatePredefinedPrice(String fullProductName, float newPrice) {
        SQLiteDatabase db = null;
        try {
            String[] parts = fullProductName.split(" - ", 2);
            if (parts.length != 2) {
                return false;
            }

            String category = parts[0];
            String name = parts[1];
            
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRICE, newPrice);

            // Update the database
            int rowsAffected = db.update(TABLE_INVENTORY,
                    values,
                    COLUMN_NAME + " = ? AND " + COLUMN_CATEGORY + " = ?",
                    new String[]{name, category});

            if (rowsAffected > 0) {
                // Update the in-memory map
                PRODUCT_PRICES.put(fullProductName, newPrice);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error updating product price: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<Order> getOrdersByDate(String date) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_ORDERS + 
                         " WHERE date(" + COLUMN_ORDER_DATE + ") = date(?)" +
                         " ORDER BY " + COLUMN_ORDER_DATE + " DESC, " + COLUMN_ORDER_TIME + " DESC";
            
            cursor = db.rawQuery(query, new String[]{date});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
                    String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
                    String orderTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TIME));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
                    
                    Order order = new Order(id, orderDate, orderTime, total, status);
                    order.setItems(getOrderItems(id));
                    orders.add(order);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting orders by date: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return orders;
    }

    public List<Order> getOrdersByDateRange(String startDate, String endDate) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_ORDERS + 
                         " WHERE date(" + COLUMN_ORDER_DATE + ") BETWEEN date(?) AND date(?)" +
                         " ORDER BY " + COLUMN_ORDER_DATE + " DESC, " + COLUMN_ORDER_TIME + " DESC";
            
            cursor = db.rawQuery(query, new String[]{startDate, endDate});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
                    String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
                    String orderTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TIME));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
                    
                    Order order = new Order(id, orderDate, orderTime, total, status);
                    order.setItems(getOrderItems(id));
                    orders.add(order);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting orders by date range: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return orders;
    }

    private List<Order.OrderItem> getOrderItems(String orderId) {
        List<Order.OrderItem> items = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            String query = "SELECT oi." + COLUMN_ORDER_PRODUCT_ID + ", " +
                         "i." + COLUMN_NAME + ", " +
                         "oi." + COLUMN_ORDER_QUANTITY + ", " +
                         "oi." + COLUMN_ORDER_ITEM_PRICE + ", " +
                         "oi." + COLUMN_ORDER_ITEM_TOTAL + " " +
                         "FROM " + TABLE_ORDER_ITEMS + " oi " +
                         "LEFT JOIN " + TABLE_INVENTORY + " i ON oi." + COLUMN_ORDER_PRODUCT_ID + " = i." + COLUMN_ID + " " +
                         "WHERE oi." + COLUMN_ORDER_ID_FK + " = ?";
            
            cursor = db.rawQuery(query, new String[]{orderId});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String productId = cursor.getString(0);
                    String productName = cursor.getString(1);
                    int quantity = cursor.getInt(2);
                    double price = cursor.getDouble(3);
                    double total = cursor.getDouble(4);
                    
                    items.add(new Order.OrderItem(productId, productName, quantity, price, total));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting order items: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        
        return items;
    }
}



