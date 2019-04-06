package com.example.rashed.uceattendance;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SemesterDialog.SemesterDialogListener,SubjectListDialog.SubjectListDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},1);

        CategoryAdapter adapter = new CategoryAdapter(this,getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onSubmitSemester(int sem) {
        RegisterFragment.mSemester = sem;
        Toast.makeText(getApplicationContext(),RegisterFragment.mSemester + " Semester is selected",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmitSubjects(ArrayList x) {
        for (int i=0;i<x.size();i++){
            RegisterFragment.selectedSubjects.add(RegisterFragment.subjectList[0][Integer.parseInt(x.get(i).toString())]);
        }
    }
}
