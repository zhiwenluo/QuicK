package com.uniquestudio.quick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.uniquestudio.asynctask.MyAsyncTask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class quickPhoto extends Activity implements SurfaceHolder.Callback,
        android.view.GestureDetector.OnGestureListener {
    /** Called when the activity is first created. */
    SurfaceHolder surfaceHolder;

    SurfaceView surfaceView1;

    Button button1;

    ImageView photo_take_bg;
//    ImageView imageView1;

    Camera camera;

    AutoFocusCallback autoFocusCallback;

    SharedPreferences sharedPreferences;

    Boolean continuation = false;

    Boolean canTap = false;

    Boolean isAutoFocus = true;

    Boolean isImediate = true;

    public static int photoQulity = 100;

    public static final String STORAGE_LOCATION = Environment
            .getExternalStorageDirectory()
            + "/Android/data/uniquesudio.QuicK/photo";

    private MyAsyncTask myAsyncTask;
    
    private Camera.Parameters parameters;

    private GestureDetector gestureDetector;

    private Animation alphaAnimation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_photo);

        sharedPreferences = getSharedPreferences("quick", MODE_PRIVATE);
        continuation = sharedPreferences.getBoolean("photo_continuous", false);
        isAutoFocus = sharedPreferences.getBoolean("photo_autofocus", true);
        isImediate = sharedPreferences.getBoolean("photo_imediate", true);
        canTap = isImediate? false:true;
        
        alphaAnimation = AnimationUtils.loadAnimation(this , R.anim.alpha);
        
        button1 = (Button) findViewById(R.id.button1);
        surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
        photo_take_bg = (ImageView) findViewById(R.id.photo_take_bg);
//        imageView1 = (ImageView) findViewById(R.id.imageView1);
        surfaceHolder = surfaceView1.getHolder();
        surfaceHolder.setKeepScreenOn(true); // 使摄像头一直保持高亮
        surfaceHolder.setFixedSize(176, 144);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        autoFocusCallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if (success) {
                    // 对焦成功拍照
                    if (camera != null)
                        camera.takePicture(null, null, jpeg);
                } else {
                    if (camera != null)
                        camera.takePicture(null, null, jpeg);
                }
                photo_take_bg.startAnimation(alphaAnimation);
                photo_take_bg.setVisibility(8);
                Toast.makeText(getApplicationContext(), "拍照成功!",
                        Toast.LENGTH_SHORT).show();
            }
        };

        gestureDetector = new GestureDetector(this);
        gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                Log.e("continuation", continuation + "");
                if (!continuation)
                    Toast.makeText(getApplicationContext(), "双击拍摄",
                            Toast.LENGTH_LONG).show();
                else {
                    continuation = false;
                    Toast.makeText(getApplicationContext(), "连拍取消",
                            Toast.LENGTH_LONG).show();
                }
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                // TODO Auto-generated method stub
                if (canTap)
                    button1.performClick();
                return true;
            }

            public boolean onDoubleTap(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                camera.setParameters(parameters);
                if (isAutoFocus) {
                    camera.autoFocus(autoFocusCallback);
                } else {
                    if (camera != null)
                        photo_take_bg.startAnimation(alphaAnimation);
                    photo_take_bg.setVisibility(8);
                        camera.takePicture(null, null, jpeg);
                    Toast.makeText(getApplicationContext(), "拍照成功!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewTreeObserver vto = surfaceView1.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                ViewTreeObserver vto2 = button1.getViewTreeObserver();
                if (isImediate) {
                vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        // TODO Auto-generated method stub
                        button1.performClick();
                        Log.e("click", "click!");
                        button1.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });}
                int height = button1.getHeight();
                int width = button1.getWidth();
                Log.e("height + weight", height + ":" + width);

                button1.setVisibility(View.GONE);// 这一句非常重要！

                surfaceView1.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
    }

    PictureCallback jpeg = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            //每次都需要ｎｅｗ一个ｉｎｓｔａｎｃｅ
            myAsyncTask = new MyAsyncTask(quickPhoto.this , 0 );
            myAsyncTask.execute(data);

            camera.startPreview();
            canTap = true;// 可以接受点击事件
            // 需要手动重新startPreview，否则停在拍下的瞬间
            if (continuation) {
                // 如果是连续拍摄情况下，仍然不能点击
                canTap = false;
                button1.performClick();
            }
        }
    };

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            camera = Camera.open();
            parameters = camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFlashMode(sharedPreferences.getBoolean("photo_flash",
                    false) ? Parameters.FLASH_MODE_TORCH
                    : Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            // 设置参数
            camera.setPreviewDisplay(surfaceHolder);
            // 摄像头画面显示在Surface上
            camera.startPreview();
            camera.setDisplayOrientation(90);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
        continuation = true;
        button1.performClick();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        System.out.println("surfaceDestroyed__destory");
        if (camera != null) {
            camera.stopPreview();
            // 关闭预览
            camera.release();
            // 释放资源
            camera = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
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
            camera.stopPreview();
            // 关闭预览
            camera.release();
            // 释放资源
            camera = null;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        // releaseMediaRecorder();
        super.onPause();
        System.out.println("surfaceDestroyed__onpause");
        if (this.camera != null) {
            camera.stopPreview();
            // 关闭预览
            camera.release();
            // 释放资源
            camera = null;
        }
        finish();
    }
    
    

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.out.println("surfaceDestroyed__onstop");
        if (camera != null) {
            camera.stopPreview();
            // 关闭预览
            camera.release();
            // 释放资源
            camera = null;
        }
    }

    public static void isDirExist(String path) {
        // 如果不存在的话，则创建存储目录
        File mediaStorageDir = new File(path);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
    }
}
