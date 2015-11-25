package com.example.qkx.multiweibo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.db.WeiboDB;
import com.example.qkx.multiweibo.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 15/11/23.
 */
public class MainActivity extends FragmentActivity {
    private static String TAG = "MainActivity";

    private List<Fragment> mList;
    private List<User> users;
    private int currIndex = 0;
    private int winWidth;
    private int bottomWidth;

    private ViewPager mViewPager;
    private LinearLayout mTagGroup;
    private LinearLayout bottomContainer;
    private ImageView ivBottomLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);


        init();
    }

    private void init() {
        mList = new ArrayList<Fragment>();

        mViewPager = (ViewPager) findViewById(R.id.vPager);
        mTagGroup = (LinearLayout) findViewById(R.id.id_tag_group);
        bottomContainer = (LinearLayout) findViewById(R.id.id_liner_bottom);
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);

        WeiboDB weiboDB = WeiboDB.getInstance(this);
        users = weiboDB.loadUsers();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        winWidth = dm.widthPixels;
        bottomWidth = winWidth / users.size();

        ivBottomLine = new ImageView(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(bottomWidth, 4);
//        ivBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
        ivBottomLine.setImageResource(R.color.white);
        ivBottomLine.setLayoutParams(p);
        bottomContainer.removeAllViews();
        bottomContainer.addView(ivBottomLine);

        for (int i = 0; i < users.size(); i++) {
            Fragment fragment = MyFragment.newInstance(users.get(i).access_token);
            mList.add(fragment);

            TextView textView = new TextView(this);
            textView.setText(users.get(i).uid);
            if (i == 0) {
                textView.setTextColor(getResources().getColor(R.color.white));
            } else {
                textView.setTextColor(getResources().getColor(R.color.lightwhite));
            }
            textView.setTextSize(13);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            params.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(params);
            mTagGroup.addView(textView);

            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(finalI, true);
                    currIndex = finalI;
                }
            });
        }

        MyFragmentPagerAdaper adaper = new MyFragmentPagerAdaper(getSupportFragmentManager(), mList);
        mViewPager.setAdapter(adaper);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            TextView tv1 = (TextView)mTagGroup.getChildAt(currIndex);
            tv1.setTextColor(getResources().getColor(R.color.lightwhite));
            TextView tv2 = (TextView)mTagGroup.getChildAt(position);
            tv2.setTextColor(getResources().getColor(R.color.white));

            Animation animation = new TranslateAnimation(bottomWidth * currIndex,bottomWidth * position,0,0);
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);

            currIndex = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
