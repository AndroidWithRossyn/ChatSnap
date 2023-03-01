package com.spacester.chatsnapsupdate.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.spacester.chatsnapsupdate.R;
import com.spacester.chatsnapsupdate.faceFilters.FaceFilters;
import com.spacester.chatsnapsupdate.user.AddStoryActivity;

public class TabView extends FrameLayout implements ViewPager.OnPageChangeListener {
    private ImageView mCenterImage;
    private ImageView mStartImage;
    private ImageView mEndImage;
    private ImageView mBottomImage;
    private View indicator;
    Context context;
    private ArgbEvaluator argbEvaluator;
    private int mCenterColor;
    private int mSlideColor;
    private int mEndViewsTranslationX;
    private int mIndicatorTranslationX;
    private int mCenterTranslationY;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    public TabView(@NonNull Context context) {
        this(context, null);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void setUpWithViewPager(final ViewPager viewPager){
        viewPager.addOnPageChangeListener(this);
        mStartImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(0);
            }
        });
        mEndImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 2)
                    viewPager.setCurrentItem(2);
            }
        });
        mCenterImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() != 1){
                    viewPager.setCurrentItem(1);
                }

            }
        });
        mBottomImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddStoryActivity.class);
                getContext().startActivity(intent);
            }
        });

        mCenterImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), FaceFilters.class);
                getContext().startActivity(intent);
            }
        });
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.tab_view, this, true);
        mCenterImage = findViewById(R.id.center_btn);
        mStartImage = findViewById(R.id.start_btn);
        mEndImage = findViewById(R.id.end_btn);
        mBottomImage = findViewById(R.id.bottom_btn);
        indicator = findViewById(R.id.indicator);
        mCenterColor = ContextCompat.getColor(getContext(), R.color.white);
        mSlideColor = ContextCompat.getColor(getContext(), R.color.grey);
        argbEvaluator = new ArgbEvaluator();
     mIndicatorTranslationX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
    mBottomImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mEndViewsTranslationX = (int) ((mBottomImage.getX() - mStartImage.getX()) - mIndicatorTranslationX);
            mBottomImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        mCenterTranslationY = getHeight() - mBottomImage.getBottom();
        }
    });



    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0){
            setColor(1 - positionOffset);
            moveViews(1 - positionOffset);
            indicator.setTranslationX((positionOffset - 1) * mIndicatorTranslationX);
       moveAndScaleCenter(1 - positionOffset);
        }else if (position == 1){
            setColor(positionOffset);
            moveViews( positionOffset);
            indicator.setTranslationX(positionOffset * mIndicatorTranslationX);
            moveAndScaleCenter(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void moveAndScaleCenter(float fractionFromCenter){
       float scale = .7f + ((1 -fractionFromCenter) * .3f);
        mCenterImage.setScaleX(scale);
        mCenterImage.setScaleY(scale);
        int translation = (int) (fractionFromCenter * mCenterTranslationY);
        mCenterImage.setTranslationY(translation);
        mBottomImage.setTranslationY(translation);
        mBottomImage.setAlpha(1 - fractionFromCenter);
    }
    private void moveViews(float fractionFromCenter){
        mStartImage.setTranslationX(fractionFromCenter * mEndViewsTranslationX);
        mEndImage.setTranslationX(-fractionFromCenter * mEndViewsTranslationX);
    indicator.setAlpha(fractionFromCenter);
    indicator.setScaleX(fractionFromCenter);
    }
    private void setColor(float fractionFromCenter){
       int color = (int) argbEvaluator.evaluate(fractionFromCenter, mCenterColor, mSlideColor);
       mCenterImage.setColorFilter(color);
        mStartImage.setColorFilter(color);
        mEndImage.setColorFilter(color);
    }

}
