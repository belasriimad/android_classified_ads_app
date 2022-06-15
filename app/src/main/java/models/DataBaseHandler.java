package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by belasri on 17/12/2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {
    private ArrayList<AdInfo> adsList = new ArrayList<>();
    public DataBaseHandler(Context context) {
        super(context,Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE "+ Constants.TABLE_NAME + "(" +
                Constants.KEY_ID + " INTEGER PRIMARY KEY ," + Constants.AD_TITLE + " TEXT ," +
                Constants.AD_DESC + " TEXT ,"+
                Constants.AD_CITY + " TEXT ," + Constants.AD_IMAGE + " TEXT," + Constants.USER_ID
                + " TEXT," + Constants.AD_ID + " TEXT);";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS" + Constants.TABLE_NAME);
        //create a new one
        onCreate(db);
    }
    //add fav
    public void addFav(String title,String desc,String city,String image,String ad_id,String user_id){
        SQLiteDatabase dba = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.AD_TITLE,title);
        values.put(Constants.AD_DESC,desc);
        values.put(Constants.AD_CITY,city);
        values.put(Constants.AD_ID,ad_id);
        values.put(Constants.AD_IMAGE,image);
        values.put(Constants.USER_ID,user_id);
        dba.insert(Constants.TABLE_NAME, null, values);
        Log.v("fav added", "yes");
        dba.close();
    }
    //delete fav
    public void deleteFavourite(String id){
        SQLiteDatabase dba = this.getReadableDatabase();
        dba.delete(Constants.TABLE_NAME, Constants.AD_ID + " = ? ", new String[]{String.valueOf(id)});
        dba.close();
    }
    //delete fav
    public Boolean isFavourite(String id){
        SQLiteDatabase dba = this.getReadableDatabase();
        String query = String.format("SELECT * FROM  adsFavs WHERE  AdId='%s';",id);
        Cursor cursor = dba.rawQuery(query,null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public ArrayList<AdInfo> getFavs(){
        adsList.clear();
        SQLiteDatabase dba = this.getReadableDatabase();
        Cursor cursor = dba.query(Constants.TABLE_NAME,new String[]{Constants.KEY_ID,Constants.AD_TITLE,Constants.AD_CITY,Constants.AD_IMAGE,Constants.AD_ID,Constants.USER_ID},null,null,null,null,Constants.KEY_ID + " DESC ");
        if(cursor.moveToNext()){
            do {
                AdInfo adInfo = new AdInfo();
                adInfo.setTitle(cursor.getString(cursor.getColumnIndex(Constants.AD_TITLE)));
                adInfo.setCity(cursor.getString(cursor.getColumnIndex(Constants.AD_CITY)));
                adInfo.setAdId(cursor.getString(cursor.getColumnIndex(Constants.AD_ID)));
                adInfo.setImageUrl(cursor.getString(cursor.getColumnIndex(Constants.AD_IMAGE)));
                adsList.add(adInfo);
                Log.d("adInfo",adInfo.getAdId());
            }while(cursor.moveToNext());
        }
        cursor.close();
        dba.close();
        return adsList;
    }

}
