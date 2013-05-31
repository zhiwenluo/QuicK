package com.uniquestudio.quick;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.uniquestudio.asynctask.MyAsyncTask;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class quickRecord extends Activity implements OnClickListener,
        android.view.GestureDetector.OnGestureListener {
    private Resources resources;

    private MyAsyncTask myAsyncTask;

    private Button btnRecord;

    public static MediaRecorder mRecorder;

    private GestureDetector gestureDetector;

    private ImageView recordLight, recordRotate;

    private Animation rotateAnimation, alphaAnimation;

    public static final String STORAGE_LOCATION = Environment
            .getExternalStorageDirectory()
            + "/Android/data/uniquesudio.QuicK/record";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_record);
        init();
        initAnimation();
    }

    @SuppressWarnings("deprecation")
    private void init() {

        recordLight = (ImageView) findViewById(R.id.record_light);
        recordRotate = (ImageView) findViewById(R.id.record_rotate);

        resources = this.getResources();

        btnRecord = (Button) findViewById(R.id.start);
        btnRecord.setOnClickListener(this);
        gestureDetector = new GestureDetector(this);
        gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "双击结束录音",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }

            public boolean onDoubleTap(MotionEvent e) {
                // TODO Auto-generated method stub
                releaseMediaRecorder();
                return false;
            }
        });

        ViewTreeObserver vto = btnRecord.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                btnRecord.performClick();
                Log.e("click", "click!");
                btnRecord.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
            }
        });
        btnRecord.setVisibility(View.GONE);
    }

    private void initAnimation() {
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        alphaAnimation = AnimationUtils
                .loadAnimation(this, R.anim.alpha_record);
        // 设置匀速转动
        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                recordLight.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
        rotateAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                recordRotate.startAnimation(animation);
            }
        });

        recordRotate.startAnimation(rotateAnimation);
        recordLight.startAnimation(alphaAnimation);
    }

    public static boolean checkSDCard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public void recording() {
        if (quickRecord.checkSDCard()) {
            String str = new String("record");
            byte[] tmp = str.getBytes();
            myAsyncTask = new MyAsyncTask(quickRecord.this, 2);
            myAsyncTask.execute(tmp);
        } else {
            String noSDCard = getString(R.string.noSDCard);
            Toast.makeText(getApplicationContext(), noSDCard, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    private void releaseMediaRecorder() {
        String recordOK = resources.getString(R.string.recordOK);
        Toast.makeText(getApplicationContext(), recordOK, Toast.LENGTH_LONG)
                .show();

        if (mRecorder != null) {
            mRecorder.reset(); // 清除recorder配置
            mRecorder.release(); // 释放recorder对象
            mRecorder = null;
        }
        finish();

    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        recording();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            releaseMediaRecorder();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        releaseMediaRecorder();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        releaseMediaRecorder();
    }

}
