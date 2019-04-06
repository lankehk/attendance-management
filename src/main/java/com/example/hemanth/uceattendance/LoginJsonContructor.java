package com.example.rashed.uceattendance;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rashed on 06-03-2018.
 */

public class LoginJsonContructor {

    public static String construct(String id,String password){
        JSONObject data = new JSONObject();
        try{
            data.put("status","login");
            data.put("id",id);
            data.put("password",password);
        }
        catch(JSONException e){
            Log.v("Error","Cannot create JSONObject" + e.getMessage());
        }
        return data.toString();
    }
}
