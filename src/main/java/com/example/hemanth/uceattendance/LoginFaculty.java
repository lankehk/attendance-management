package com.example.rashed.uceattendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginFaculty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_faculty);

        final ArrayList<Subject> subjects = new ArrayList<>();

        try{
            JSONArray data = new JSONArray(LoginFragment.subjectData);
            for (int i=0;i<data.length();i++){
                JSONObject subject = data.getJSONObject(i);
                subjects.add(new Subject(subject.getString("code"),subject.getString("name"),subject.getInt("classes")));
            }
        }
        catch(JSONException e){
            Log.v("Error","Cannot create Array from subject data");
        }

        SubjectAdapter adapter = new SubjectAdapter(this,subjects);
        ListView list = findViewById(R.id.facultyList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjects.get(position);
                CodeGenerateDialog dialog = new CodeGenerateDialog();
                Bundle arg = new Bundle();
                arg.putString("title",subject.getSub());
                arg.putString("code",subject.getSubCode());
                arg.putString("id",LoginFragment.empNo);
                dialog.setArguments(arg);
                dialog.show(getFragmentManager(),"Code Generate");
            }
        });
    }
}
