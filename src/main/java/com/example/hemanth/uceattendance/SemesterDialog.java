package com.example.rashed.uceattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Rashed on 28-02-2018.
 */

public class SemesterDialog extends DialogFragment {

    private int mSelectedItem;

    public interface SemesterDialogListener{
        public void onSubmitSemester(int sem);
    }

    SemesterDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (SemesterDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement SemesterDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Semester")
                .setSingleChoiceItems(R.array.semesters, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedItem = which+1;
                    }
                })
                .setPositiveButton("OK", null);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog!=null){
            Button positive = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectedItem == 0){
                        Toast.makeText(getContext(),"Please Select Semester",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mListener.onSubmitSemester(mSelectedItem);
                        dialog.dismiss();
                    }
                }
            });
        }
    }
}
