package com.example.rashed.uceattendance;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class LoginStudent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);

        final ArrayList<Subject> subjects = new ArrayList<>();

        TextView attendanceView = (TextView) findViewById(R.id.total_attendance);

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
        ListView list = findViewById(R.id.studentList);
        list.setAdapter(adapter);
        attendanceView.setText(LoginFragment.totalAttendance);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjects.get(position);
                Bundle arg = new Bundle();
                arg.putString("title",subject.getSub());
                arg.putString("rollno",LoginFragment.rollNo);
                CodeEntryDialog codeEntryDialog = new CodeEntryDialog();
                codeEntryDialog.setArguments(arg);
                codeEntryDialog.show(getFragmentManager(),"Code Submit");
            }
        });
    }
}
