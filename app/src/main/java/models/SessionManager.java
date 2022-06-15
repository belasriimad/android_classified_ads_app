package models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.belasri.hiresell.R.drawable.user;

/**
 * Created by belasri on 11/12/2017.
 */

public class SessionManager {
    public static SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER = "user_id";
    public SessionManager(Context context){
        this.context = context;
        prefs = this.context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = prefs.edit();
    }
    public void addSession(String id){
        editor.putString(KEY_USER,id);
        editor.commit();
    }
    public String getData(){
        String user_id = prefs.getString(KEY_USER,null);
        return user_id;
    }
}
