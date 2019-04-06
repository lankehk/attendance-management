package com.example.rashed.uceattendance;

/**
 * Created by Rashed on 10-02-2018.
 */

public class Subject {

    private String mSubCode;
    private String mSub;
    private int mClasses;

    public Subject(String subCode, String sub, int classes) {
        mSubCode = subCode;
        mSub = sub;
        mClasses = classes;
    }

    public String getSubCode() {
        return mSubCode;
    }

    public String getSub() {
        return mSub;
    }

    public int getClasses() {
        return mClasses;
    }
}
