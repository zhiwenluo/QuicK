package com.uniquestudio.quick;

import java.io.File;

import javax.security.auth.PrivateCredentialPermission;

import com.uniquestudio.filechooser.FileChooserDialog;
import com.uniquestudio.guide.GuideViewActivity;
import com.uniquestudio.view.SwitchButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class QuicK extends Activity {

    // 判断ｔｈｒｅａｄ中的message
    private static final int MSG_SHUT = 0;

    private static final int MSG_EXPAND = 1;

    // 判断对哪一个进行动画
    private static final int CAMERA_CLOSE = 0;

    private static final int CAMERA_OPEN = 1;

    private static final int VIDEO_CLOSE = 2;

    private static final int VIDEO_OPEN = 3;

    private static final int ABOUT_CLOSE = 4;

    private static final int ABOUT_OPEN = 5;

    private int threadMessage;

    private static final String PACKAGE_NAME = "com.uniquestudio.quick";

    private static final String SCHEME = "package";

    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

    private static final String APP_PKG_NAME_22 = "pkg";

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    public static final String STORAGE_LOCATION = Environment
            .getExternalStorageDirectory() + "/Android/data/uniquesudio.QuicK";

    public static final String STORAGE_LOCATION_PHOTO = STORAGE_LOCATION
            + File.separator + "photo";

    public static final String STORAGE_LOCATION_VIDEO = STORAGE_LOCATION
            + File.separator + "video";

    public static final String STORAGE_LOCATION_RECORD = STORAGE_LOCATION
            + File.separator + "record";

    private boolean isExpand_photo = true, isExpand_video = false,
            isExpand_about = false, measured = false;

    private boolean isAnimating = false;

    private SharedPreferences sharedPreferences;

    private Editor editor;

    // 用于点击控制伸缩的一级控件中的一部分
    private ImageButton photoTitle, videoTitle, aboutTitle, helpTitle,
            storageTitle;

    // 用于点击控制伸缩的一级控件,包裹整个.
    private LinearLayout cameraViewGroup, aboutViewGroup, videoViewGroup;

    // 所有的小三角形
    private ImageView sanjiao_video, sanjiao_storage, sanjiao_help,
            sanjiao_about_us, theLastSanjiao;

    private ImageView blue_light;

    private ImageView camera_sanjiao_child, video_sanjiao_child,
            about_sanjiao_child;

    // 用于点击控制伸缩的一级控件的一部分 , 被***ViewGroup包裹着
    private RelativeLayout groupCamera, groupVideo, groupStorage, groupHelp,
            groupAboutUs;

    // 伸缩的二级控件, 被group***包裹着
    // private RelativeLayout childCamera_1, childCamera_2, childCamera_3,
    // childCamera_4, childVedio, childAboutUs_1, childAboutUs_2;

    private SwitchButton photoImediate, photoContinuous, photoFlash,
            photoAutoFocus, vedioFlash;

    // 记录下高度
    private int cameraHeight, videoHeight, aboutUsHeight;

    // 记录下目前的高度
    private int myViewHeight;
    
    private float mFirstDownY; // 首次按下的Y

    private float mFirstDownX; // 首次按下的X

    private Thread thread;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            LinearLayout.LayoutParams linear = (LinearLayout.LayoutParams) ((ViewGroup) msg.obj)
                    .getLayoutParams();
            switch (threadMessage) {
            case CAMERA_CLOSE:
            case CAMERA_OPEN:
                myViewHeight += cameraHeight / 8;
                break;
            case VIDEO_CLOSE:
            case VIDEO_OPEN:
                myViewHeight += videoHeight / 4;
                break;
            default:
                break;
            }
            
            
            switch (msg.arg1) {
            case MSG_SHUT:
                // myViewHeight += msg.arg2 / 4;

                linear.height = msg.arg2 - myViewHeight;
                if (linear.height > 0) {

                } else if (linear.height <= 0) {
                    Log.e("fffffff", "I come here!!!!!!!!!close");
                    handler.removeCallbacks(runnable);
                    linear.height = 0;
                    myViewHeight = 0;
                    isAnimating = false;

                    switch (threadMessage) {
                    case CAMERA_CLOSE:
                        groupCamera
                                .setBackgroundResource(R.drawable.blue_bg_fang);
                        sanjiao_video.setVisibility(0);
                        blue_light.setVisibility(8);
                        break;

                    case VIDEO_CLOSE:
                        groupVideo
                                .setBackgroundResource(R.drawable.blue_bg_fang);
                        sanjiao_storage.setVisibility(0);
                        break;

//                    case ABOUT_CLOSE:
//                        groupAboutUs
//                                .setBackgroundResource(R.drawable.blue_bg_fang);
//                        theLastSanjiao.setVisibility(0);
//                        break;
                    default:
                        break;
                    }

                }
                ((ViewGroup) msg.obj).setLayoutParams(linear);
                break;
            case MSG_EXPAND:
                // myViewHeight += msg.arg2 / 4;
                linear.height = myViewHeight;
                if (linear.height < msg.arg2) {

                } else if (linear.height >= msg.arg2) {
                    Log.e("fffffff", "I come here!!!!!!!!!open");
                    handler.removeCallbacks(runnable);
                    linear.height = msg.arg2;
                    myViewHeight = 0;
                    isAnimating = false;

                }
                ((ViewGroup) msg.obj).setLayoutParams(linear);
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_main);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        isInstallOnSDCard(PACKAGE_NAME);
        initData();
        initView();
        setOnclickListener();
        // 适配
        // DisplayMetrics metric = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(metric);
        // int width = metric.widthPixels; // 屏幕宽度（像素）
        // int height = metric.heightPixels; // 屏幕高度（像素）
        // float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        // int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        // Log.e("dip", densityDpi+"");

    }

    private void initData() {

        sharedPreferences = getSharedPreferences("quick", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Log.e("fffffffff", cameraHeight + "---" + videoHeight + "---" +
        // aboutUsHeight);
        // 展开后的子项
        cameraViewGroup = (LinearLayout) findViewById(R.id.cameraViewGroup);
        aboutViewGroup = (LinearLayout) findViewById(R.id.AboutViewGroup);
        videoViewGroup = (LinearLayout) findViewById(R.id.child_video);
        // childVedio = (RelativeLayout) findViewById(R.id.child_vedio_1);

        // 点击伸缩的标题+小三角(避免点到小三角不伸缩)
        groupCamera = (RelativeLayout) findViewById(R.id.camera);
        groupVideo = (RelativeLayout) findViewById(R.id.vedio);
        groupStorage = (RelativeLayout) findViewById(R.id.storage);
        groupHelp = (RelativeLayout) findViewById(R.id.help);
        groupAboutUs = (RelativeLayout) findViewById(R.id.about_us);

        // 点击伸缩的标题(换背景)
        photoTitle = (ImageButton) findViewById(R.id.group_camera);
        videoTitle = (ImageButton) findViewById(R.id.group_vedio);
        aboutTitle = (ImageButton) findViewById(R.id.group_about_us);
        helpTitle = (ImageButton) findViewById(R.id.group_help);
        storageTitle = (ImageButton) findViewById(R.id.group_storage);

        // 开关switch
        photoImediate = (SwitchButton) findViewById(R.id.photo_imediate);
        photoContinuous = (SwitchButton) findViewById(R.id.photo_continuous);
        photoFlash = (SwitchButton) findViewById(R.id.photo_flash);
        photoAutoFocus = (SwitchButton) findViewById(R.id.photo_autofocus);
        vedioFlash = (SwitchButton) findViewById(R.id.vedio_flash);

        // 小三角形
        sanjiao_about_us = (ImageView) findViewById(R.id.sanjiao_about_us);
        sanjiao_help = (ImageView) findViewById(R.id.sanjiao_help);
        sanjiao_storage = (ImageView) findViewById(R.id.sanjiao_storage);
        sanjiao_video = (ImageView) findViewById(R.id.sanjiao_vedio);
        theLastSanjiao = (ImageView) findViewById(R.id.the_last_sanjiao);

        blue_light = (ImageView) findViewById(R.id.blue_light);
        // camera_sanjiao_child = (ImageView)
        // findViewById(R.id.camera_sanjiao_child);
        // video_sanjiao_child = (ImageView)
        // findViewById(R.id.video_sanjiao_child);
        about_sanjiao_child = (ImageView) findViewById(R.id.about_sanjiao_child);

        // 存储高度,

        ViewTreeObserver vto = cameraViewGroup.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!measured) {
                    cameraHeight = cameraViewGroup.getHeight();
                    videoHeight = videoViewGroup.getHeight();
                    aboutUsHeight = aboutViewGroup.getHeight();
                    // 测玩高度后重置video和about高度为0
                    LinearLayout.LayoutParams linearlayout = (LinearLayout.LayoutParams) videoViewGroup
                            .getLayoutParams();
                    linearlayout.height = 0;
                    videoViewGroup.setLayoutParams(linearlayout);
//                    linearlayout = (LinearLayout.LayoutParams) aboutViewGroup
//                            .getLayoutParams();
//                    linearlayout.height = 0;
//                    aboutViewGroup.setLayoutParams(linearlayout);
                    Log.e("fffffffff", cameraHeight + "---" + videoHeight
                            + "---" + aboutUsHeight);
                    measured = true;
                }
            }
        });

    }

    private void initView() {
        // TODO Auto-generated method stub
        photoImediate.setCheckedDelayed(sharedPreferences.getBoolean(
                "photo_imediate", true));
        photoContinuous.setCheckedDelayed(sharedPreferences.getBoolean(
                "photo_continuous", false));
        photoFlash.setCheckedDelayed(sharedPreferences.getBoolean(
                "photo_flash", false));
        photoAutoFocus.setCheckedDelayed(sharedPreferences.getBoolean(
                "photo_autofocus", true));
        vedioFlash.setCheckedDelayed(sharedPreferences.getBoolean(
                "video_flash", false));

    }

    public void setOnclickListener() {
        //title_background
        groupCamera.setOnClickListener(new groupClickListener());
        groupCamera.setId(1);
        groupVideo.setOnClickListener(new groupClickListener());
        groupVideo.setId(2);
        groupStorage.setOnClickListener(new groupClickListener());
        groupStorage.setId(3);
        groupHelp.setOnClickListener(new groupClickListener());
        groupHelp.setId(4);
        groupAboutUs.setOnClickListener(new groupClickListener());
        groupAboutUs.setId(5);
        
//        groupStorage.setOnTouchListener(new OnTouchListener() {
//            
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                
//                int action = event.getAction();
//                float x = event.getX();
//                float y = event.getY();
//                float deltaX = Math.abs(x - mFirstDownX);
//                float deltaY = Math.abs(y - mFirstDownY);
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        mFirstDownX = x;
//                        mFirstDownY = y;
//                        groupStorage.setBackgroundResource(R.drawable.grey_bg_fang);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        groupStorage.setBackgroundResource(R.drawable.blue_bg_fang);
//                        break;
//                }
//                return true;
//            }
//        });
        //title
        photoTitle.setOnClickListener(new groupClickListener());
        photoTitle.setId(6);
        videoTitle.setOnClickListener(new groupClickListener());
        videoTitle.setId(7);
        aboutTitle.setOnClickListener(new groupClickListener());
        aboutTitle.setId(8);
        helpTitle.setOnClickListener(new groupClickListener());
        helpTitle.setId(14);
        storageTitle.setOnClickListener(new groupClickListener());
        storageTitle.setId(15);
        //checkbox
        photoImediate.setOnCheckedChangeListener(new checkBoxListener());
        photoImediate.setId(9);
        photoContinuous.setOnCheckedChangeListener(new checkBoxListener());
        photoContinuous.setId(10);
        photoFlash.setOnCheckedChangeListener(new checkBoxListener());
        photoFlash.setId(11);
        photoAutoFocus.setOnCheckedChangeListener(new checkBoxListener());
        photoAutoFocus.setId(12);
        vedioFlash.setOnCheckedChangeListener(new checkBoxListener());
        vedioFlash.setId(13);
    }

    class checkBoxListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            // TODO Auto-generated method stub

            switch (buttonView.getId()) {
            case 9:
                editor.putBoolean("photo_imediate", isChecked).commit();
                break;
            case 10:
                editor.putBoolean("photo_continuous", isChecked).commit();
                break;
            case 11:
                editor.putBoolean("photo_flash", isChecked).commit();
                break;
            case 12:
                editor.putBoolean("photo_autofocus", isChecked).commit();
                break;
            case 13:
                editor.putBoolean("video_flash", isChecked).commit();
                break;
            default:
                break;
            }
        }

    }

    class groupClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case 1:
            case 6:
                // camera
                if (!isAnimating) {

                    isExpand_photo = !isExpand_photo;

                    if (isExpand_photo) {
                        threadMessage = CAMERA_OPEN;
                        groupCamera
                                .setBackgroundResource(R.drawable.grey_bg_fang);
                        sanjiao_video.setVisibility(8);
                        blue_light.setVisibility(0);
                    } else {
                        threadMessage = CAMERA_CLOSE;

                    }
                    thread = new Thread(runnable);
                    thread.start();
                }

                break;
            case 2:
            case 7:
                // video
                if (!isAnimating) {
                    isExpand_video = !isExpand_video;

                    if (isExpand_video) {
                        threadMessage = VIDEO_OPEN;
                        groupVideo
                                .setBackgroundResource(R.drawable.grey_bg_fang);
                        sanjiao_storage.setVisibility(8);
                    } else {
                        threadMessage = VIDEO_CLOSE;
                    }
                    thread = new Thread(runnable);
                    thread.start();
                }
                break;
            case 3:
            case 15:
                // 存储位置
                if (quickRecord.checkSDCard()) {

                    quickPhoto.isDirExist(STORAGE_LOCATION_PHOTO);
                    quickPhoto.isDirExist(STORAGE_LOCATION_VIDEO);
                    quickPhoto.isDirExist(STORAGE_LOCATION_RECORD);
                    // 调用文件管理器
                    Intent intent = new Intent(getBaseContext(),
                            FileChooserDialog.class);
                    intent.putExtra(FileChooserDialog.START_PATH,
                            STORAGE_LOCATION);
                    intent.putExtra(FileChooserDialog.SELECT_MODE,
                            FileChooserDialog.MODE_OPEN);
                    startActivityForResult(intent, 1);

                } else {
                    String noSDCard = getString(R.string.noSDCard);
                    Toast.makeText(getApplicationContext(), noSDCard,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
            case 14:
                // 帮助
                Intent intent_help = new Intent(QuicK.this,
                        GuideViewActivity.class);
                intent_help.putExtra("id", 1);
                startActivity(intent_help);
                finish();
                break;
            case 5:
            case 8:
                // 关于我们
                // if (!isAnimating) {
                isExpand_about = !isExpand_about;

                if (isExpand_about) {
                    groupAboutUs.setBackgroundResource(R.drawable.grey_bg_fang);
                    aboutViewGroup.setVisibility(0);
                    theLastSanjiao.setVisibility(8);
                    // threadMessage = ABOUT_OPEN;

                } else {
                    // threadMessage = ABOUT_CLOSE;
                    groupAboutUs.setBackgroundResource(R.drawable.blue_bg_fang);
                    about_sanjiao_child
                            .setBackgroundResource(R.drawable.blue_bg_sanjiao);
                    aboutViewGroup.setVisibility(8);
                    theLastSanjiao.setVisibility(0);
                }
                // thread = new Thread(runnable);
                // thread.start();
                // }
                break;
            default:
                break;
            }
        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub

            Message message = new Message();
            switch (threadMessage) {
            case CAMERA_CLOSE:
                message.arg1 = MSG_SHUT;
                message.arg2 = cameraHeight;
                message.obj = cameraViewGroup;
                break;
            case CAMERA_OPEN:
                message.arg1 = MSG_EXPAND;
                message.arg2 = cameraHeight;
                message.obj = cameraViewGroup;
                break;
            case VIDEO_CLOSE:
                message.arg1 = MSG_SHUT;
                message.arg2 = videoHeight;
                message.obj = videoViewGroup;
                break;
            case VIDEO_OPEN:
                message.arg1 = MSG_EXPAND;
                message.arg2 = videoHeight;
                message.obj = videoViewGroup;
                break;
//            case ABOUT_CLOSE:
//                message.arg1 = MSG_SHUT;
//                message.arg2 = aboutUsHeight;
//                message.obj = aboutViewGroup;
//                break;
//            case ABOUT_OPEN:
//                message.arg1 = MSG_EXPAND;
//                message.arg2 = aboutUsHeight;
//                message.obj = aboutViewGroup;
//                break;

            default:
                break;
            }

            handler.sendMessage(message);
            handler.postDelayed(runnable, 5);
            isAnimating = true;
        }
    };

    // 跳转至转移app界面
    public static void showInstalledAppDetails(Context context,
            String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    // 判断app是否安装在sd卡上
    public void isInstallOnSDCard(String packageName) {
        PackageManager pm = this.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo(packageName, 0);
            // 在sdcard里
            if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                AlertDialog.Builder builder = new Builder(this);
                builder.setMessage("建议您将本应用安装至手机内存中,是否现在转移?");
                builder.setTitle("检测到安装在SDCard中");
                builder.setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                showInstalledAppDetails(QuicK.this,
                                        "com.uniquestudio.quick");
                            }
                        });
                builder.setPositiveButton("不转移",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                return;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return;
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        initView();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        initView();

        super.onResume();
    }

}