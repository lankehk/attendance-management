package com.example.rashed.uceattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Rashed on 08-03-2018.
 */

public class CodeEntryDialog extends DialogFragment {

    private Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflater.inflate(R.layout.code_entry_dialog,null))
                .setTitle(getArguments().getString("title"))
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
        if (dialog!=null){
            mContext = dialog.getContext();
            final EditText codeInput = (EditText) dialog.findViewById(R.id.code_input);
            Button submitAttendance = (Button) dialog.findViewById(R.id.submit_attendance);

            submitAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String code = codeInput.getText().toString();
                    String data = studentJsonConstructor(code,getArguments().getString("rollno"));
                    new StudentAttendanceAsyncTask().execute(data);
                }
            });
        }
    }

    private String studentJsonConstructor(String code, String rollno){
        JSONObject result = new JSONObject();
        try{
            result.put("status","studentAttendance");
            result.put("rollno",rollno);
            result.put("code",code);
        }catch (JSONException e){}
        return result.toString();
    }

    class StudentAttendanceAsyncTask extends AsyncTask<String, Void, String>{

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
                return data;
            }
            catch(IOException e){
                Log.v("Error ","error while connecting");
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
        }
    }
}
