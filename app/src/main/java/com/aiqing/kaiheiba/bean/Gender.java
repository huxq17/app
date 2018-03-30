package com.aiqing.kaiheiba.bean;


import com.aiqing.kaiheiba.R;

public enum Gender {
    MALE(R.mipmap.prof_male_n), FEMALE(R.mipmap.prof_female_n), UNKNOWLE(R.mipmap.prof_unknow_);
    int id;

    Gender(int id) {
        this.id = id;
    }

    public int getResId() {
        return id;
    }
}
