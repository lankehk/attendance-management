package com.example.rashed.uceattendance;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rashed on 10-02-2018.
 */

public class SubjectAdapter extends ArrayAdapter<Subject> {

    public SubjectAdapter(Activity context, ArrayList<Subject> subjects){
        super(context,0, subjects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.subject_list,parent,false);
        }

        Subject currentSubject = getItem(position);

        TextView subCodeView = listItemView.findViewById(R.id.sub_code);
        subCodeView.setText(currentSubject.getSubCode());

        TextView subView = listItemView.findViewById(R.id.sub);
        subView.setText(currentSubject.getSub());

        TextView classesView = listItemView.findViewById(R.id.classes);
        classesView.setText(String.valueOf(currentSubject.getClasses()));

        return listItemView;
    }
}
