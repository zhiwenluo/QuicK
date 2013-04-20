package com.uniquestudio.quick;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.uniquestudio.asynctask.MyAsyncTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class quickVideo extends Activity implements View.OnClickListener,
        android.view.GestureDetector.OnGestureListener {
    private SurfaceView sfvShow;

    private SurfaceHolder mHolder;

    private TextView timeText;

    private Button btnRecord;

    private Button btnStop;

    public static MediaRecorder mediaRecorder;

    private Camera camera = null;

    private Camera.CameraInfo cameraInfo;

    private Camera.Parameters parameters;

    private SharedPreferences sharedPreferences;
    
    private int cameraCount = 0;

    private Timer timer = new Timer();
    
    private MyAsyncTask myAsyncTask;

    private int vedioSeconds = 0;

    public static boolean isRecording = false;

    public static boolean frontOrBack = true;// 前后摄像头
    
    private boolean isFlashOpen;

    private GestureDetector gestureDetector;

    public static final String STORAGE_LOCATION = Environment.getExternalStorageDirectory() + "/Android/data/uniquesudio.QuicK/video";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
            case 1:
                vedioSeconds += 1000;
                String str = new SimpleDateFormat("mm:ss").format(vedioSeconds);
                timeText.setText(str);
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置窗口无标题
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        setContentView(R.layout.quick_video);
        initView();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences("quick", MODE_PRIVATE);
        isFlashOpen = sharedPreferences.getBoolean("video_flash", false);
        btnRecord = (Button) this.findViewById(R.id.start);
        btnStop = (Button) this.findViewById(R.id.stop);
        timeText = (TextView) this.findViewById(R.id.timeCount);
        btnRecord.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        sfvShow = (SurfaceView) this.findViewById(R.id.sfv);
        mHolder = sfvShow.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFixedSize(176, 144);
        mHolder.setKeepScreenOn(true); // 使摄像头一直保持高亮

        ViewTreeObserver vto = sfvShow.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                ViewTreeObserver vto2 = btnRecord.getViewTreeObserver();
                vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        // TODO Auto-generated method stub
                        btnRecord.performClick();
                        Log.e("click", "click!");
                        btnRecord.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
                int height = btnRecord.getHeight();
                int width = btnRecord.getWidth();
                Log.e("height + weight", height + ":" + width);
                btnRecord.setVisibility(View.GONE);// 这一句话看似简单，其实非常重要！！！
                sfvShow.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });

        gestureDetector = new GestureDetector(this);
        gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "双击结束拍摄",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                // TODO Auto-generated method stub
                // 停止录像
                if (isRecording) {
                    releaseMediaRecorder();
                }
                return true;
            }

            public boolean onDoubleTap(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    // 触摸屏幕事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            releaseMediaRecorder();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initCamera() throws IOException {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                try {
                    camera = Camera.open(camIdx);
                    camera.setDisplayOrientation(90);
                    parameters = camera.getParameters();
                    parameters.setFlashMode(isFlashOpen ? Parameters.FLASH_MODE_TORCH:Parameters.FLASH_MODE_OFF);//FLASH_MODE_TORCH
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(mHolder);
                    camera.startPreview();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        camera.unlock();
    }

    @SuppressLint("NewApi")
    private void releaseMediaRecorder() {
        String videoOk  =  getString(R.string.viedo_ok);
        Toast.makeText(getApplicationContext(), videoOk, Toast.LENGTH_LONG)
                .show();

        if (mediaRecorder != null) {
            isRecording = false;
            mediaRecorder.stop();
            mediaRecorder.reset(); // 清除recorder配置
            mediaRecorder.release(); // 释放recorder对象
            mediaRecorder = null;
            camera.lock(); // 为后续使用锁定摄像头
            camera.release();
            camera = null;
            timer.cancel();
        }

        finish();

    }

    public void onClick(View v) {
        // TODO Auto-generated method stub

        try {
            initCamera(); // 调用前置摄像头--注意，要在MediaRecorder设置参数之前就调用unlock来获得camera的控制权。camera是单例的嘛。如果不调用，程序就挂

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(camera); // 如果需要前置摄像头，则加上，反之，这句话不需要存在
            // 设置声音从哪个设备获取
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            // 设置画面从哪个设备获取
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setProfile(CamcorderProfile
                    .get(CamcorderProfile.QUALITY_HIGH));
            
            // 显示画面
            mediaRecorder.setPreviewDisplay(mHolder.getSurface()); // 预览输入
            //异步保存
            String str = new String("video");
            byte[] tmp = str.getBytes();
            myAsyncTask = new MyAsyncTask(quickVideo.this , 1);
            myAsyncTask.execute(tmp);
            
            isRecording = true;
            setTimerTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
            float arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
            float arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        releaseMediaRecorder();
        super.onDestroy();
    }

    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        releaseMediaRecorder();
    }

    //显示秒数
    private void setTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.e("timecount", vedioSeconds + "");

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        releaseMediaRecorder();
        finish();
    }
    
}
