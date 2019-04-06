package com.example.rashed.uceattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Rashed on 08-03-2018.
 */

public class CodeGenerateDialog extends DialogFragment {

    private Bundle args;

    private String code;

    private String timer;

    private Context mContext;

    TextView countdownTimer;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        args = getArguments();
        builder.setView(inflater.inflate(R.layout.code_dialog, null))
                .setTitle(args.getString("title"))
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            mContext = dialog.getContext();
            final LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            final TextView attendanceCode = (TextView) dialog.findViewById(R.id.attendance_code);
            countdownTimer = (TextView) dialog.findViewById(R.id.countdown_timer);
            final Button codeGenerator = (Button) dialog.findViewById(R.id.code_generate);
            final EditText rollNo = (EditText) dialog.findViewById(R.id.rollno);
            final Button giveAttendance = (Button) dialog.findViewById(R.id.give_attendance);
            final Button done=(Button) dialog.getButton(Dialog.BUTTON_POSITIVE);

            final CountDownTimer timer = new CountDownTimer(11000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    countdownTimer.setText(millisUntilFinished/1000 + " seconds until code expiry");
                }

                @Override
                public void onFinish() {
                    countdownTimer.setText("Code Expired");
                    rollNo.setVisibility(View.VISIBLE);
                    giveAttendance.setVisibility(View.VISIBLE);
                    rollNo.setLayoutParams(linearLayoutParams);
                    giveAttendance.setLayoutParams(relativeLayoutParams);
                }
            };
            codeGenerator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codeGenerator.setEnabled(false);
                    attendanceCode.setVisibility(View.VISIBLE);
                    countdownTimer.setVisibility(View.VISIBLE);
                    attendanceCode.setLayoutParams(linearLayoutParams);
                    countdownTimer.setLayoutParams(linearLayoutParams);
                    attendanceCode.setTextSize(20);
                    countdownTimer.setTextSize(15);
                    new AttendanceCode().execute(codeJsonConstructor());
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){}
                    attendanceCode.setText(code);
                    timer.start();
                }
            });

            giveAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rollNo.length() == 12 && rollNo.getText().toString().matches("1005" + "[0-9]{2}" + "733" + "[0-9]{3}")){
                        String rollno = rollNo.getText().toString();
                        new AttendanceCode().execute(giveJsonConstructor(rollno));
                    }
                    else{
                        Toast.makeText(mContext,"Please Enter Valid ID",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AttendanceCode().execute(submitJsonConstructor());
                    dismiss();
                }
            });
        }
    }

    private String submitJsonConstructor()
    {
        JSONObject result=new JSONObject();
        try{
            result.put("status","submitAttendance");
            result.put("code",code);
        }
        catch(JSONException e){

        }
        return result.toString();

    }

    private String giveJsonConstructor(String rollno){
        JSONObject result = new JSONObject();
        try{
            result.put("status","giveAttendance");
            result.put("rollno",rollno);
            result.put("code",code);
        }
        catch (JSONException e){

        }
        return result.toString();
    }

    private String codeJsonConstructor(){
        JSONObject result = new JSONObject();
        try{
            result.put("status","codeGenerator");
            result.put("code",args.getString("code"));
            result.put("id",args.getString("id"));
        }
        catch (JSONException e){

        }
        return result.toString();
    }

    private String randomCodeGenerator() {
        String code = "";
        Random random = new Random();
        code = String.valueOf(random
                .nextInt((9 * (int) Math.pow(10, 7)) - 1)
                + (int) Math.pow(10, 7));
        return code;
    }

    class AttendanceCode extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            DataOutputStream out;
            DataInputStream in;
            try{
                String matter = strings[0];
                Socket client = new Socket("10.10.12.61",8117);

                out = new DataOutputStream(client.getOutputStream());
                in = new DataInputStream(client.getInputStream());
                out.writeUTF(matter);
                out.flush();
                String data = in.readUTF();
                if(data.length()==8){
                    code = data;
                }
                return data;
            }
            catch(IOException e){
                Log.v("Error ","error while connecting");
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.length()!=8){
                Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
            }
        }
    }

}
