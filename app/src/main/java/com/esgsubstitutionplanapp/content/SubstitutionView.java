package com.esgsubstitutionplanapp.content;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.esgsubstitutionplanapp.content.model.SubstitutionType;

public class SubstitutionView extends LinearLayout {

    private String date;
    private SubstitutionType substitutionType;

    public SubstitutionView(Context context) {
        super(context);
    }

    public SubstitutionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SubstitutionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SubstitutionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSubstitutionType(SubstitutionType substitutionType) {
        this.substitutionType = substitutionType;
    }

    public String getDate() {
        return date;
    }

    public SubstitutionType getSubstitutionType() {
        return substitutionType;
    }
}
