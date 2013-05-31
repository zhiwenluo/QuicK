package com.uniquestudio.quick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.uniquestudio.asynctask.MyAsyncTask;

import android.R.layout;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class quickPhoto extends Activity implements SurfaceHolder.Callback,
        android.view.GestureDetector.OnGestureListener {
    /** Called when the activity is first created. */
    SurfaceHolder surfaceHolder;

    SurfaceView surfaceView1;

    Button button1;

    ImageView photo_take_bg;

    // ImageView imageView1;

    Camera camera;

    AutoFocusCallback autoFocusCallback;

    SharedPreferences sharedPreferences;

    Boolean continuation = false;

    Boolean canTap = false;

    Boolean isAutoFocus = true;

    Boolean isImediate = true;

    Boolean photo_flash = false;

    List<Camera.Size> sizes;

    List<Camera.Size> psizes;

    Size size;

    Size pSize;

    private ImageButton photoFlash;

    public static int photoQulity = 100;

    public static final String STORAGE_LOCATION = Environment
            .getExternalStorageDirectory()
            + "/Android/data/uniquesudio.QuicK/photo";

    private MyAsyncTask myAsyncTask;

    private Camera.Parameters parameters;

    private GestureDetector gestureDetector;

    private Animation alphaAnimation;

    private boolean havaSurfaceView = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_photo);

        sharedPreferences = getSharedPreferences("quick", MODE_PRIVATE);
        continuation = sharedPreferences.getBoolean("photo_continuous", false);
        isAutoFocus = sharedPreferences.getBoolean("photo_autofocus", true);
        isImediate = sharedPreferences.getBoolean("photo_imediate", true);
        photo_flash = sharedPreferences.getBoolean("photo_flash", false);
        canTap = isImediate ? false : true;

        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha);

        photoFlash = (ImageButton) findViewById(R.id.flash_photo_bt);
        button1 = (Button) findViewById(R.id.button1);
        surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
        photo_take_bg = (ImageView) findViewById(R.id.photo_take_bg);
        // imageView1 = (ImageView) findViewById(R.id.imageView1);
        surfaceHolder = surfaceView1.getHolder();
        surfaceHolder.setKeepScreenOn(true); // 使摄像头一直保持高亮
        surfaceHolder.setFixedSize(176, 144);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        autoFocusCallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                // 拍照时的界面闪光效果
                photo_take_bg.startAnimation(alphaAnimation);
                photo_take_bg.setVisibility(8);

                if (success) {
                    // 对焦成功拍照
                    if (camera != null)
                        camera.takePicture(null, null, jpeg);
                } else {
                    if (camera != null)
                        camera.takePicture(null, null, jpeg);
                }

                Toast.makeText(getApplicationContext(), "拍照成功!",
                        Toast.LENGTH_SHORT).show();
            }
        };

        gestureDetector = new GestureDetector(this);
        gestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {

            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                String long_press = getString(R.string.long_press);
                Toast.makeText(getApplicationContext(), long_press,
                        Toast.LENGTH_LONG).show();
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e) {
                // TODO Auto-generated method stub
                Log.e("continuation", continuation + "");
                if (!continuation) {
                    if (canTap)
                        button1.performClick();
                }
                // Toast.makeText(getApplicationContext(), "双击拍摄",
                // Toast.LENGTH_LONG).show();
                else {
                    continuation = false;
                    Toast.makeText(getApplicationContext(), "连拍取消",
                            Toast.LENGTH_LONG).show();
                }

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
                if (parameters == null) {
                    Log.e("fffffffffff", "meicamera");
                }
                camera.setParameters(parameters);
                if (isGif) {
                    // 进行gif

                } else {
                    //
                    if (isAutoFocus) {
                        camera.autoFocus(autoFocusCallback);
                    } else {
                        if (camera != null) {
                            // 拍照时的界面闪光效果
                            photo_take_bg.startAnimation(alphaAnimation);
                            photo_take_bg.setVisibility(8);
                            camera.takePicture(null, null, jpeg);
                            Toast.makeText(getApplicationContext(), "拍照成功!",
                                    Toast.LENGTH_SHORT).show();
                        }//if camera != null
                    }// if isAutoFocus
                }// if isGif
            }
        });
        // 闪光灯的开关
        photoFlash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (photo_flash) {
                    parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    photoFlash
                            .setBackgroundResource(R.drawable.widget_photo_pressed);// off
                } else {
                    parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    photoFlash.setBackgroundResource(R.drawable.widget_photo);// on
                }
                photo_flash = !photo_flash;
                camera.setParameters(parameters);

            }
        });
        //
        // if (havaSurfaceView) {
        //
        // ViewTreeObserver vto = surfaceView1.getViewTreeObserver();
        // vto.addOnGlobalLayoutListener(new onGlobalLayoutListener());
        // }
    }

    class onGlobalLayoutListener implements OnGlobalLayoutListener {

        @Override
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
                });
            }
            int height = button1.getHeight();
            int width = button1.getWidth();
            Log.e("height + weight", height + ":" + width);

            button1.setVisibility(View.GONE);// 这一句非常重要！

            surfaceView1.getViewTreeObserver().removeGlobalOnLayoutListener(
                    this);
        }
    }

    PreviewCallback previewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if (quickRecord.checkSDCard()) {
                if (data != null) {
                    Camera.Parameters p = camera.getParameters();
                    p.setPreviewFrameRate(4);// 设置每秒取的帧数
                    camera.setParameters(p);

                    int width = p.getPreviewSize().width;
                    int height = p.getPreviewSize().height;
                    Log.e("fuck", "preview width: " + width + " height: "
                            + height);
                    if (width > 320 || height > 240) {
                        // 有些手机显示的比较大
                        Toast.makeText(quickPhoto.this,
                                "width " + width + " height " + height,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                String noSDCard = getString(R.string.noSDCard);
                Toast.makeText(getApplicationContext(), noSDCard,
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    PictureCallback jpeg = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            // 每次都需要ｎｅｗ一个ｉｎｓｔａｎｃｅ

            if (quickRecord.checkSDCard()) {
                myAsyncTask = new MyAsyncTask(quickPhoto.this, 0);
                myAsyncTask.execute(data);

                camera.startPreview();
                canTap = true;// 可以接受点击事件
                // 需要手动重新startPreview，否则停在拍下的瞬间
                if (continuation) {
                    // 如果是连续拍摄情况下，仍然不能点击
                    canTap = false;
                    button1.performClick();
                }
            } else {
                String noSDCard = getString(R.string.noSDCard);
                Toast.makeText(getApplicationContext(), noSDCard,
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            camera = Camera.open();
            parameters = camera.getParameters();
            // 获取支持的分辨率
            sizes = parameters.getSupportedPictureSizes();
            psizes = parameters.getSupportedPreviewSizes();
            size = sizes.get(0);
            pSize = psizes.get(0);
            Log.e("fffffff", size.height + " " + size.width);

            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFlashMode(sharedPreferences.getBoolean("photo_flash",
                    false) ? Parameters.FLASH_MODE_TORCH
                    : Parameters.FLASH_MODE_OFF);
            parameters.set("rotation", 90);
            parameters.setPreviewSize(pSize.width, pSize.height);
            // parameters.setPictureSize(size.width , size.height);
            camera.setParameters(parameters);
            // 设置参数
            camera.setPreviewDisplay(surfaceHolder);
            // 摄像头画面显示在Surface上

            camera.setDisplayOrientation(90);
            camera.startPreview();
            havaSurfaceView = true;

            ViewTreeObserver vto = surfaceView1.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new onGlobalLayoutListener());
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
