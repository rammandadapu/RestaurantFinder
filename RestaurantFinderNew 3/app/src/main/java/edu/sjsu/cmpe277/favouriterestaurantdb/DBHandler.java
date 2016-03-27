package edu.sjsu.cmpe277.favouriterestaurantdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sjsu.cmpe277.restaurantfindernew.NavActivity;

/**
 * Created by ram.mandadapu on 3/20/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;
    private static final String DB = "restaurant.db";
    private static final String TABLE = "myrestaurant";
    private static final String TABLE_RESTAURANT_NAME = "name";
    private static final String TABLE_RESTAURANT_LOCATION = "location";
    private static final String TABLE_RESTAURANT_ADDRESS = "address";
    private static final String TABLE_RESTAURANT_PHONE = "phone";
    private static final String TABLE_RESTAURANT_URL = "url";
    private static final String TABLE_RESTAURANT_RATING_URL = "rating_img_url";
    private static final String TABLE_RESTAURANT_ID = "_id";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB, factory, DB_VERSION);
    }

    /**
     * Creates the DB
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE + " ( "
                + TABLE_RESTAURANT_ID + " TEXT PRIMARY KEY,"
                + TABLE_RESTAURANT_NAME + " TEXT,"
                + TABLE_RESTAURANT_LOCATION + " TEXT,"
                + TABLE_RESTAURANT_ADDRESS + " TEXT,"
                + TABLE_RESTAURANT_PHONE + " TEXT,"
                + TABLE_RESTAURANT_URL + " TEXT,"
                + TABLE_RESTAURANT_RATING_URL + " TEXT "
                + ")";
        db.execSQL(query);

    }

    /**
     * Creates new table if version number changes
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE);
        onCreate(db);
    }

    /**
     * Add the restaurant
     * @param restaurant
     */
    public void addRestaurant(Restaurant restaurant) {

        ContentValues values = new ContentValues();
        values.put(TABLE_RESTAURANT_ID, restaurant.getBusinessId());
        values.put(TABLE_RESTAURANT_NAME, restaurant.getName());
        values.put(TABLE_RESTAURANT_LOCATION, restaurant.getLocation());
        values.put(TABLE_RESTAURANT_ADDRESS, restaurant.getAddress());
        values.put(TABLE_RESTAURANT_PHONE, restaurant.getPhone());
        values.put(TABLE_RESTAURANT_URL, restaurant.getUrl());
        values.put(TABLE_RESTAURANT_RATING_URL, restaurant.getRatingUrl());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE, null, values);
        db.close();
    }

    public void deleteRestaurant(String _id) {

        String query = "delete  from " + TABLE + " where " + TABLE_RESTAURANT_ID + " like '" + _id + "'";
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(query);

    }

    /**
     * Return list of all favourite restaurants. If nothing exist returns empty list.
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllRestaurants() {

        String query = "select * from " + TABLE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<HashMap<String, String>> restaurantList = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            HashMap<String, String> restaurant = new HashMap<>();
            restaurant.put(NavActivity.TAG_RES_ID, cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_ID)));
            restaurant.put(NavActivity.TAG_RES_NAME, cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_NAME)));
            restaurant.put(NavActivity.TAG_RES_ADDRESS, cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_ADDRESS)));
            restaurant.put(NavActivity.TAG_RES_BUSINESSIMAGE, cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_URL)));
            restaurant.put(NavActivity.TAG_RES_RATINGIMAGE, cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_RATING_URL)));

            cursor.moveToNext();
            /*restaurant.setPhone(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_PHONE)));
            restaurant.setUrl(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_URL)));
            restaurant.setLocation(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_LOCATION)));*/
            restaurantList.add(restaurant);
        }
        cursor.close();
        db.close();;
        return restaurantList;
    }

    public ArrayList<String> getAllRestaurantIds() {

        String query = "select * from " + TABLE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> restaurantIdsList = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            restaurantIdsList.add(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_ID)));
            cursor.moveToNext();
            /*restaurant.setPhone(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_PHONE)));
            restaurant.setUrl(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_URL)));
            restaurant.setLocation(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_LOCATION)));*/

        }
        cursor.close();
        db.close();
        return restaurantIdsList;
    }

    public ArrayList<String> getAllRestaurantNames() {

        String query = "select * from " + TABLE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> restaurantNamesList = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            restaurantNamesList.add(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_NAME)));
            cursor.moveToNext();
            /*restaurant.setPhone(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_PHONE)));
            restaurant.setUrl(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_URL)));
            restaurant.setLocation(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_LOCATION)));*/

        }
        cursor.close();
        db.close();
        return restaurantNamesList;
    }

    /**
     * Returns a particular restaurant if exists else returns NULL
     * @param _id
     * @return
     */
    public Restaurant getRestaurants(String _id) {
        String query = "select * from " + TABLE + " where " + TABLE_RESTAURANT_ID + " is like '" + _id + "'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Restaurant restaurant = null;
        while (!cursor.isAfterLast()) {

            restaurant = new Restaurant();
            restaurant.setBusinessId(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_ID)));
            restaurant.setName(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_NAME)));
            restaurant.setAddress(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_ADDRESS)));
            restaurant.setPhone(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_PHONE)));
            restaurant.setUrl(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_URL)));
            restaurant.setRatingUrl(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_RATING_URL)));
            restaurant.setLocation(cursor.getString(cursor.getColumnIndex(TABLE_RESTAURANT_LOCATION)));

        }

        return restaurant;
    }

    /**
     * To check if already favourite
     * @param _id
     * @return
     */
    public boolean isFavourite(String _id) {
        String query = "select * from " + TABLE + " where " + TABLE_RESTAURANT_ID + " like '" + _id + "'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return !cursor.isAfterLast();
    }


}
