package com.esri.natmoapp.ui.introslide;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.esri.natmoapp.auth.login.LoginActivity;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class IntroSlideActivityVM extends ActivityViewModel<IntroSlideActivity> {

    IntroSlideInteractor interactor;
    int count = 0;
    int NUM_PAGES = 3;
    int currentPage = 0;
    boolean touched = false;
    Handler handler = new Handler();
    Runnable update;

    public IntroSlideActivityVM(IntroSlideActivity activity, IntroSlideInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        startPagerAutoSwipe();
        IntializeeventHandlers();
    }

    public void shownextsltide() {
        Intent intent = new Intent(activity, LoginActivity.class);
        //intent.putExtra("usertype", "Public");
        activity.startActivity(intent);
    }

    public void IntializeeventHandlers() {
        activity.tabLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touched = true;
                        return true;

                    case MotionEvent.ACTION_UP:
                        touched = false;
                        return true;
                }
                return false;
            }
        });
    }

    private void startPagerAutoSwipe() {
        update = new Runnable() {
            public void run() {
                if (!touched) {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    activity.tabLayout.getTabAt(currentPage++).select();
                    /* pager.setCurrentItem(currentPage++, true);*/
                }
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 3000);
    }

}
