package com.example.gezijoke.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.libcommon.PixUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PPImageView extends AppCompatImageView {
    public PPImageView(@NonNull Context context) {
        super(context);
    }

    public PPImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PPImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * requireAll = true 时，需要在布局文件中写两个参数，才会调用
     * 为fasle时，满足其中一个就会调用
     *
     * @param view
     * @param imageUrl
     * @param isCircle
     */
    @BindingAdapter(value = {"image_url", "isCircle"}, requireAll = false)
    public static void setImageUrl(PPImageView view, String imageUrl, boolean isCircle) {

        RequestBuilder<Drawable> builder = Glide.with(view).load(imageUrl);

        if (isCircle) {
            builder.transform(new CircleCrop());
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            builder.override(layoutParams.width, layoutParams.height);
        }
        builder.into(view);
    }

    public void bindData(int widthPx, int heightPx, int marginLeft, String imgUrl) {
            bindData(widthPx, heightPx, marginLeft,
                    PixUtils.getScreenWidth(),
                    PixUtils.getScreenHeight(),
                    imgUrl);
    }

    public void bindData(int widthPx, int heightPx, int marginLeft, int maxWidth, int maxheight, String imgUrl) {
        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imgUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    int height = resource.getIntrinsicHeight();
                    int width = resource.getIntrinsicWidth();
                    setSize(width, height, marginLeft, maxWidth, maxheight);
                    setImageDrawable(resource);
                }
            });
        }
        setSize(widthPx, heightPx, marginLeft, maxWidth, maxheight);
        setImageUrl(this, imgUrl, false);
    }

    private void setSize(int width, int height, int marginLeft, int maxWidth, int maxheight) {
        int finalWidth, finalHeight;
        if (width > height) {
            finalWidth = maxWidth;
            finalHeight = (int) (height / (width * 1.0f / finalWidth));
        } else {
            finalHeight = maxheight;
            finalWidth = (int) (width / (height * 1.0f / finalHeight));
        }
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                finalWidth, finalHeight
        );
        params.leftMargin = height > width ?
                PixUtils.dp2px(marginLeft) :
                0;

        setLayoutParams(params);
    }

    public void setBlurImageUrl(String coverUrl, int radius) {
        Glide.with(this).load(coverUrl)
                .override(50)
                .transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        setBackground(resource);
                    }
                });

    }
}
