package com.example.rashed.uceattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Rashed on 10-02-2018.
 */

public class LoginFragment extends Fragment {

    public static String subjectData = new String();

    static String rollNo = new String();

    static String empNo = new String();

    static String totalAttendance = new String();

    public LoginFragment() {

    }

    public boolean checkPermission(View v){
        return ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public String getUniqueId(View v){
        boolean permission = checkPermission(v);
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;// = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(permission == true){
            if("9774d56d682e549c".equals(deviceId) || deviceId == null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    deviceId = telephonyManager.getImei();
                    Log.v("Id","Imei Id = " + deviceId);
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
        final View rootView = inflater.inflate(R.layout.activity_login, container, false);
        final RadioGroup loginList = rootView.findViewById(R.id.login_list);
        final RadioButton faculty = rootView.findViewById(R.id.login_faculty);
        final RadioButton student = rootView.findViewById(R.id.login_student);
        final Button login = rootView.findViewById(R.id.login);
        final String studentCode = "1005", course = "733";
        final String facultyCode[] = new String[] {"480","805","801"};
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = Toast.LENGTH_LONG;
                CharSequence text = null;
                Intent loginActivity = null;
                if(loginList.getCheckedRadioButtonId() == -1){
                    text = "Please Select the Designation";
                }
                else{
                    String uniqueId = uniqueIdGenerator.generate(getUniqueId(rootView),"project");
                    EditText loginId = rootView.findViewById(R.id.loginId);
                    boolean facultyCheck = false, studentCheck = false;
                    if(faculty.isChecked()){
                        facultyCheck = true;
                    }
                    else if(student.isChecked()){
                        studentCheck = true;
                    }
                    if(uniqueId != null){
                        if(facultyCheck == true){
                            if((loginId.length() == 5 || loginId.length() == 6) && (loginId.getText().toString().matches(facultyCode[0] + "[0-9]{2}") || loginId.getText().toString().matches(facultyCode[1] + "[0-9]{2}") || loginId.getText().toString().matches(facultyCode[2] + "[0-9]{3}"))){
                                new LoginAsyncTask().execute(LoginJsonContructor.construct(loginId.getText().toString(),uniqueId));
                                try{
                                    Thread.sleep(100);
                                }
                                catch(InterruptedException e){}
                                text = "Welcome";
                                empNo = loginId.getText().toString();
                                loginActivity = new Intent(getActivity(),LoginFaculty.class);
                            }
                            else{
                                text = "Please Enter Valid ID";
                            }
                        }
                        else if(studentCheck == true){

                            if(loginId.length() == 12 && loginId.getText().toString().matches(studentCode + "[0-9]{2}" + course + "[0-9]{3}")){
                                new LoginAsyncTask().execute(LoginJsonContructor.construct(loginId.getText().toString(),uniqueId));
                                try{
                                    Thread.sleep(100);
                                }
                                catch(InterruptedException e){}
                                text = "Welcome";
                                rollNo = loginId.getText().toString();
                                loginActivity = new Intent(getActivity(),LoginStudent.class);
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
                Toast message = Toast.makeText(getContext(),text,duration);
                message.show();
                if(loginActivity != null){
                    try {
                        Thread.sleep(100);
                    }catch(InterruptedException e){
                        Log.v("Error", "Sleep Interrupted");
                    }
                    startActivity(loginActivity);
                }
            }
        });
        return rootView;
    }

    class LoginAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            DataOutputStream out;
            DataInputStream in;
            String data = strings[0];
            try{
                Socket client = new Socket("10.10.12.61",8117);
                out = new DataOutputStream(client.getOutputStream());
                in = new DataInputStream(client.getInputStream());
                out.writeUTF(data);
                out.flush();
                JSONObject subjects = new JSONObject(in.readUTF());
                totalAttendance = subjects.getString("total_attendance");
                subjectData = subjects.getJSONArray("subjects").toString();
                return subjectData;
            }
            catch (IOException e){
                Log.v("Error","Cannot connect to Server" + e.getMessage());
                return "Error";
            }
            catch(JSONException e){
                return "Cannot create JSON object";
            }
        }
    }
}
