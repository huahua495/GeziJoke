package com.example.libcommon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ConnerLinearLayout extends LinearLayout {

    public ConnerLinearLayout(Context context) {
        this(context, null);
    }

    public ConnerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ConnerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        ViewHelper.setViewOutline(this, attrs, defStyleAttr, defStyleRes);
    }
}
