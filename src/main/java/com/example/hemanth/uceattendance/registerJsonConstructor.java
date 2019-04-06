package com.example.rashed.uceattendance;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rashed on 16-02-2018.
 */

public class registerJsonConstructor {
    public static String construct(String id, String password, int sem){
        JSONObject data = new JSONObject();
        try{
            data.put("status","register");
            data.put("id",id);
            data.put("password",password);
            data.put("sem",String.valueOf(sem));
        }
        catch(JSONException e){
            Log.v("Error ",e.getMessage());
        }
        return data.toString();
    }

    public static String construct(String id, String password,String subjects){
        JSONObject data = new JSONObject();
        try{
            data.put("status","register");
            data.put("id",id);
            data.put("password",password);
            data.put("subjects",subjects);
            Log.v("data",data.toString());
        }
        catch(JSONException e){
            Log.v("Error ",e.getMessage());
        }
        return data.toString();
    }
}
