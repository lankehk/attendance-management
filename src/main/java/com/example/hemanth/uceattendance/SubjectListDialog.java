package com.example.rashed.uceattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Rashed on 02-03-2018.
 */

public class SubjectListDialog extends DialogFragment {

    private ArrayList mSelectedItems;

    public interface SubjectListDialogListener{
        public void onSubmitSubjects(ArrayList x);
    }

    SubjectListDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (SubjectListDialog.SubjectListDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement SubjectListDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Subjects")
                .setMultiChoiceItems(RegisterFragment.subjectList[1],null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            mSelectedItems.add(which);
                        }
                        else if(mSelectedItems.contains(which)){
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
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
                    if(mSelectedItems.isEmpty()){
                        Toast.makeText(getContext(),"Please Select the Subjects you Teach",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Collections.sort(mSelectedItems);
                        mListener.onSubmitSubjects(mSelectedItems);
                        dialog.dismiss();
                    }
                }
            });
        }
    }
}
