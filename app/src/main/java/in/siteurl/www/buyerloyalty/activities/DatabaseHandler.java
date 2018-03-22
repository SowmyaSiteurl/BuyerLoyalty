package in.siteurl.www.buyerloyalty.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import in.siteurl.www.buyerloyalty.core.RedeemProduct;


/**
 * Created by siteurl on 3/1/18.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "redeenProducts.db";
    public static final String TABLE_REDEEM_CART = "redeem";

    // Contacts Table Columns names
    public static final String REDEEM_PRODUCT_ID = "redeemption_prod_id";
    public static final String REDEEM_PRODUCT_NAME = "product_name";
    public static final String REDEEM_USER_ID = "user_id";
    public static final String REDEEM_POINTS_VALUE = "points_value";
    public static final String REDEEM_PRODUCT_IMG = "product_img";
    public static final String REDEEM_UPDATED_AT = "updated_at";
    public static final String REDEEM_EXPIRY_DATE = "expiry_date";
    public static final String REDEEM_PROD_DESCRIPTION = "prod_description";
    public static final String REDEEM_TERMS_CONDITION = "terms_and_condition";
    public static final String REDEEM_STATUS = "status";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE " + TABLE_REDEEM_CART + "(" + REDEEM_PRODUCT_ID + " TEXT," + REDEEM_PRODUCT_NAME + " TEXT,"
                + REDEEM_USER_ID + " TEXT," + REDEEM_POINTS_VALUE + " TEXT," + REDEEM_PRODUCT_IMG + " TEXT,"
                + REDEEM_UPDATED_AT + " TEXT," + REDEEM_EXPIRY_DATE + " TEXT," + REDEEM_PROD_DESCRIPTION + " TEXT," + REDEEM_TERMS_CONDITION + " TEXT," + REDEEM_STATUS + " TEXT )";

        db.execSQL(table);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REDEEM_CART);

        // Create tables again
        onCreate(db);
    }


    public void addToCart(RedeemProduct redeemProduct) {

        Log.d("addToCart", redeemProduct.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(REDEEM_PRODUCT_ID, redeemProduct.getRedeemption_prod_id());
        values.put(REDEEM_PRODUCT_NAME, redeemProduct.getProduct_name());
        values.put(REDEEM_USER_ID, redeemProduct.getUser_id());
        values.put(REDEEM_POINTS_VALUE, redeemProduct.getPoints_value());
        values.put(REDEEM_PRODUCT_IMG, redeemProduct.getProduct_img());
        values.put(REDEEM_UPDATED_AT, redeemProduct.getUpdated_at());
        values.put(REDEEM_EXPIRY_DATE, redeemProduct.getExpiry_date());
        values.put(REDEEM_PROD_DESCRIPTION, redeemProduct.getProd_description());
        values.put(REDEEM_TERMS_CONDITION, redeemProduct.getTerms_and_condition());
        values.put(REDEEM_STATUS, redeemProduct.getStatus());

        db.insert(TABLE_REDEEM_CART, null, values);
        db.close();
    }

    // Getting All Cart Products
    public ArrayList<RedeemProduct> getCartProducts() {

        ArrayList<RedeemProduct> redeemProductArrayList = new ArrayList<RedeemProduct>();

        String selectQuery = "SELECT * FROM " + TABLE_REDEEM_CART;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RedeemProduct redeemProduct = new RedeemProduct();

                redeemProduct.setRedeemption_prod_id(cursor.getString(cursor.getColumnIndex(REDEEM_PRODUCT_ID)));
                redeemProduct.setProduct_name(cursor.getString(cursor.getColumnIndex(REDEEM_PRODUCT_NAME)));
                redeemProduct.setUser_id((cursor.getString(cursor.getColumnIndex(REDEEM_USER_ID))));
                redeemProduct.setPoints_value(cursor.getString(cursor.getColumnIndex(REDEEM_POINTS_VALUE)));
                redeemProduct.setProduct_img(cursor.getString(cursor.getColumnIndex(REDEEM_PRODUCT_IMG)));
                redeemProduct.setUpdated_at(cursor.getString(cursor.getColumnIndex(REDEEM_UPDATED_AT)));
                redeemProduct.setExpiry_date(cursor.getString(cursor.getColumnIndex(REDEEM_EXPIRY_DATE)));
                redeemProduct.setProd_description(cursor.getString(cursor.getColumnIndex(REDEEM_PROD_DESCRIPTION)));
                redeemProduct.setTerms_and_condition(cursor.getString(cursor.getColumnIndex(REDEEM_TERMS_CONDITION)));
                redeemProduct.setStatus(cursor.getString(cursor.getColumnIndex(REDEEM_STATUS)));

                redeemProductArrayList.add(redeemProduct);
            } while (cursor.moveToNext());
        }
        Log.d("getCartProducts()", redeemProductArrayList.toString());
        return redeemProductArrayList;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM redeem"); //delete all rows in a table
        db.close();
    }


    public void deleteSingleProduct(RedeemProduct redeemProduct) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_REDEEM_CART, REDEEM_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(redeemProduct.getRedeemption_prod_id())});
        db.close();

    }

    public void dropAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REDEEM_CART);
        onCreate(db);
    }


}