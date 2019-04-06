package com.example.rashed.uceattendance;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Rashed on 10-02-2018.
 */

public class RegisterFragment extends Fragment{

    private Context mContext;

    public static int mSemester;

    private boolean subjectsRetrieved = false;

    public static String[][] subjectList = new String[2][117];

    public static ArrayList<String> selectedSubjects = new ArrayList<String>();

    public boolean checkPermission(View v){
        return ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public String getUniqueId(View v){
        boolean permission = checkPermission(v);
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;// = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(permission){
            if("9774d56d682e549c".equals(deviceId) || deviceId == null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    deviceId = telephonyManager.getImei();
                } else {
                    deviceId = telephonyManager.getDeviceId();
                }
            }
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        return deviceId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_register, container, false);
        final RadioGroup registerList = rootView.findViewById(R.id.register_list);
        final RadioButton faculty = rootView.findViewById(R.id.register_faculty);
        final RadioButton student = rootView.findViewById(R.id.register_student);
        final String studentCode = "1005", course = "733";
        final String facultyCode[] = new String[] {"480","805","801"};
        mContext = getContext();
        new SubjectAsyncTask().execute();
        Button register = rootView.findViewById(R.id.register);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SemesterDialog semesterDialog = new SemesterDialog();
                semesterDialog.show(getFragmentManager(),"semesterDialog");
            }
        });
        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!subjectsRetrieved){
                    faculty.setChecked(false);
                    new SubjectAsyncTask().execute();
                    Toast.makeText(getContext(),"Please wait until subjects are retrieved",Toast.LENGTH_SHORT).show();
                }
                else{
                    SubjectListDialog subjectListDialog = new SubjectListDialog();
                    subjectListDialog.show(getFragmentManager(),"subjectListDialog");
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = null;
                if(registerList.getCheckedRadioButtonId()==-1){
                    text = "Please Select the Designation";
                }
                else{
                    String uniqueId = uniqueIdGenerator.generate(getUniqueId(rootView),"project");
                    EditText registerId = rootView.findViewById(R.id.registerId);
                    boolean facultyCheck = false, studentCheck = false;
                    if(faculty.isChecked()){
                        facultyCheck = true;
                    }
                    else if(student.isChecked()){
                        studentCheck = true;
                    }
                    if(uniqueId != null){
                        if(facultyCheck){
                            if((registerId.length() == 5 || registerId.length() == 6) && (registerId.getText().toString().matches(facultyCode[0] + "[0-9]{2}") || registerId.getText().toString().matches(facultyCode[1] + "[0-9]{2}") || registerId.getText().toString().matches(facultyCode[2] + "[0-9]{3}"))){
                                new RegisterAsyncTask().execute(registerJsonConstructor.construct(registerId.getText().toString(),uniqueId,selectedSubjects.toString()));
                            }
                            else{
                                text = "Please Enter Valid ID";
                            }
                        }
                        else if(studentCheck){
                            if(registerId.length() == 12 && registerId.getText().toString().matches(studentCode + "[0-9]{2}" + course + "[0-9]{3}")){
                                new RegisterAsyncTask().execute(registerJsonConstructor.construct(registerId.getText().toString(),uniqueId,mSemester));
                            }
                            else{
                                text = "Please Enter Valid ID";
                            }
                        }
                    }
                    else{
                        text = "Please Give Permission";
                    }
                }
                if(text != null){
                    Toast message = Toast.makeText(mContext,text,duration);
                    message.show();
                }
            }
        });

        return rootView;
    }

    class RegisterAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {

            DataOutputStream dataOutputStream;
            DataInputStream dataInputStream;
            try{
                String matter = strings[0];
                Socket client = new Socket("10.10.12.61",8117);
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                dataInputStream = new DataInputStream(client.getInputStream());
                dataOutputStream.writeUTF(matter);
                dataOutputStream.flush();
                String statusCode = dataInputStream.readUTF();
                return statusCode;
            }
            catch(IOException e){
                Log.e("msg: ","error while connecting" + e.getMessage());
                return "Error!!!";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
        }
    }

    class SubjectAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            DataOutputStream out;
            DataInputStream in;
            try{
                String matter = "{\"status\":\"subjects\"}";
                Socket client = new Socket("10.10.12.61",8117);
                out = new DataOutputStream(client.getOutputStream());
                in = new DataInputStream(client.getInputStream());
                out.writeUTF(matter);
                out.flush();
                String jsonData = in.readUTF();
                return jsonData;
            }
            catch(IOException e){
                Log.v("Error ","error while connecting");
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONArray subjects = new JSONArray(s);
                for (int i=0;i<subjects.length();i++){
                    subjectList[0][i] = subjects.getJSONObject(i).getString("code");
                    subjectList[1][i] = subjects.getJSONObject(i).getString("name");
                }
                subjectsRetrieved = true;
            }
            catch (JSONException e){
                Log.v("Error","parsing string to json array");
            }
        }
    }

}
