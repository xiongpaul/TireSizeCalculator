package io.github.xiongpaul.tiresizecalculator;

/*
    this is the database helper class for the Garage Database that will hold information regarding each user's list of vehicles
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CarDB {

    // database constants
    public static final String DB_NAME = "car.db";
    public static final int DB_VERSION = 4;

    // car table constants
    public static final String CAR_TABLE = "car";

    public static final String CAR_ID = "_id";
    public static final int CAR_ID_COL = 0;

    public static final String CAR_YEAR = "year";
    public static final int CAR_YEAR_COL = 1;

    public static final String CAR_MAKE = "make";
    public static final int CAR_MAKE_COL = 2;

    public static final String CAR_MODEL = "model";
    public static final int CAR_MODEL_COL = 3;

    public static final String CAR_TREAD_WIDTH = "tread_width";
    public static final int CAR_TREAD_WIDTH_COL = 4;

    public static final String CAR_ASPECT_RATIO = "aspect_ratio";
    public static final int CAR_ASPECT_RATIO_COL = 5;

    public static final String CAR_RIM_DIAMETER = "rim_diameter";
    public static final int CAR_RIM_DIAMETER_COL = 6;

    // create table statement
    public static final String CREATE_CAR_TABLE =
            "CREATE TABLE " + CAR_TABLE + " (" +
                    CAR_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CAR_YEAR            + " INTEGER NOT NULL, " +
                    CAR_MAKE            + " TEXT NOT NULL, " +
                    CAR_MODEL           + " TEXT NOT NULL, " +
                    CAR_TREAD_WIDTH     + " INTEGER NOT NULL, " +
                    CAR_ASPECT_RATIO    + " INTEGER NOT NULL, " +
                    CAR_RIM_DIAMETER    + " INTEGER NOT NULL);";

    // drop table statement
    public static final String DROP_CAR_TABLE =
            "DROP TABLE IF EXISTS " + CAR_TABLE + ";";


    // inner private static class for DBHelper which extends SQLiteOpenHelper
    private static class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create table
            db.execSQL(CREATE_CAR_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // drop table
            db.execSQL(CarDB.DROP_CAR_TABLE);
            onCreate(db);
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public CarDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    // public methods
    public Car getCar(int id) {
        String where = CAR_ID + "= ?";
        String[] whereArgs = { Integer.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(CAR_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Car car = getCarFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return car;
    }

    private static Car getCarFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                Car car = new Car(
                        cursor.getInt(CAR_ID_COL),
                        cursor.getInt(CAR_YEAR_COL),
                        cursor.getString(CAR_MAKE_COL),
                        cursor.getString(CAR_MODEL_COL),
                        cursor.getInt(CAR_TREAD_WIDTH_COL),
                        cursor.getInt(CAR_ASPECT_RATIO_COL),
                        cursor.getInt(CAR_RIM_DIAMETER_COL));
                return car;
            }
            catch(Exception e) {
                return null;
            }
        }
    }

    public long insertCar(Car car) {
        ContentValues cv = new ContentValues();

        //year, make, model, tread, aspect, diameter
        cv.put(CAR_YEAR, car.getYear());
        cv.put(CAR_MAKE, car.getMake());
        cv.put(CAR_MODEL, car.getModel());
        cv.put(CAR_TREAD_WIDTH, car.getTreadWidth());
        cv.put(CAR_ASPECT_RATIO, car.getAspectRatio());
        cv.put(CAR_RIM_DIAMETER, car.getRimDiameter());

        this.openWriteableDB();
        long rowID = db.insert(CAR_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    ArrayList<HashMap<String, String>> getCars(){
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        openReadableDB();
        Cursor cursor = db.rawQuery("SELECT _id, year, make, model FROM car",null );
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("_id", cursor.getString(0));
            map.put("year", cursor.getString(1));
            map.put("make", cursor.getString(2));
            map.put("model", cursor.getString(3));
            data.add(map);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return data;
    }

    public int deleteCar(long id) {
        String where = CAR_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openWriteableDB();
        int rowCount = db.delete(CAR_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

}

