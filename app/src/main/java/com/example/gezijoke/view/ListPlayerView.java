package com.example.gezijoke.view;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gezijoke.R;
import com.example.libcommon.PixUtils;

public class ListPlayerView extends FrameLayout {

    private View bufferView;
    private PPImageView cover, blur;
    private ImageView playBtn;

    private String mVideoUrl;
    private String mCategpry;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);

        bufferView = findViewById(R.id.buffer_view);
        cover = findViewById(R.id.cover);
        blur = findViewById(R.id.blur_background);
        playBtn = findViewById(R.id.play_btn);
    }


    public void bindData(String categpry, int widthPx,
                         int heightPx, String coverUrl, String videoUrl) {
        mVideoUrl = videoUrl;
        mCategpry = categpry;

        cover.setImageUrl(cover, coverUrl, false);

        if (widthPx < heightPx) {
            blur.setBlurImageUrl(coverUrl, 10);
            blur.setVisibility(VISIBLE);
        } else {
            blur.setVisibility(INVISIBLE);
        }
        setSize(widthPx, heightPx);
    }

    private void setSize(int widthPx, int heightPx) {
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth;
        int coverHeight;

        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / widthPx * 1.0f / maxWidth);
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);


        ViewGroup.LayoutParams blurParams = blur.getLayoutParams();

        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        blur.setLayoutParams(blurParams);

        FrameLayout.LayoutParams coverParams = (LayoutParams) cover.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverParams);


        FrameLayout.LayoutParams playBtnParams = (LayoutParams) playBtn.getLayoutParams();
        playBtnParams.gravity=Gravity.CENTER;
        playBtn.setLayoutParams(playBtnParams);
    }
}
