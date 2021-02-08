package com.example.libcommon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.example.libcommon.R;

import java.util.jar.Attributes;

public class ViewHelper {

    public static final int RADIUS_ALL = 0;
    public static final int RADIUS_LEFT = 1;
    public static final int RADIUS_TOP = 2;
    public static final int RADIUS_RIGHT = 3;
    public static final int RADIUS_BOTTOM = 4;

    public static void setViewOutline(View view, AttributeSet attributeSet
            , int defStyleAttr, int defStyleRes) {
        TypedArray array = view.getContext().obtainStyledAttributes(
                attributeSet, R.styleable.viewOutLineStrategy,
                defStyleAttr, defStyleRes
        );

        int radius = array.getDimensionPixelOffset(R.styleable.viewOutLineStrategy_clip_radius, 0);
        int radiusSide = array.getIndex(R.styleable.viewOutLineStrategy_clip_side);
        array.recycle();

        setViewOutLine(view, radius, radiusSide);

    }

    public static void setViewOutLine(View view, final int radius, final int radiusSide) {
        if (radius <= 0) {
            return;
        }

        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int width = view.getWidth();
                int height = view.getHeight();
                if ((width <= 0) || height <= 0) {
                    return;
                }

                if (radiusSide != RADIUS_ALL) {
                    int left = 0, top = 0, right = width, bottom = height;
                    if (radiusSide == RADIUS_LEFT) {
                        right += radius;
                    } else if (radiusSide == RADIUS_TOP) {
                        bottom += radius;
                    } else if (radiusSide == RADIUS_RIGHT) {
                        left -= radius;
                    } else if (radiusSide == RADIUS_BOTTOM) {
                        top -= radius;
                    }
                    outline.setRoundRect(left, top, right, bottom, radius);
                    return;
                }

                int top = 0, bottom = height, left = 0, right = width;
                if (radius <= 0) {
                    outline.setRect(left, top, right, bottom);
                } else {
                    outline.setRoundRect(left, top, right, bottom, radius);
                }
            }
        });
        view.setClipToOutline(radius > 0);
        view.invalidate();
    }
}
