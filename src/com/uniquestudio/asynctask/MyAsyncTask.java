package com.uniquestudio.asynctask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.uniquestudio.filechooser.FileChooserDialog;
import com.uniquestudio.quick.R;
import com.uniquestudio.quick.quickPhoto;
import com.uniquestudio.quick.quickRecord;
import com.uniquestudio.quick.quickVideo;

import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class MyAsyncTask extends AsyncTask<byte[], String, String>{

    private File quickFile;
    private Activity activity;
    private int id;
    private MediaRecorder recorder;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private Notification baseNF;  
    
    public MyAsyncTask(Activity activity , int id) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.id = id;
    }
//    public MyAsyncTask(Activity activity , MediaRecorder recorder , int id) {
//        this.activity = activity;
//        this.recorder = recorder;
//        this.id = id;
//    }

    @Override
    protected String doInBackground(byte[]... params) {
        // TODO Auto-generated method stub
        if (quickRecord.checkSDCard()) {
        switch (id) {
        case 0:
            //保存图片
            String string = new String(params[0]);
//            if (!string.equals("1")) {Log.e("fffffffffffffff", string);}; 
            Bitmap bmp = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
            // byte数组转换成Bitmap
            // 拍下图片显示在下面的ImageView里
            FileOutputStream fop;
            try {
                quickPhoto.isDirExist(quickPhoto.STORAGE_LOCATION);
                quickFile = new File(quickPhoto.STORAGE_LOCATION,
                        new SimpleDateFormat("yyyyMMdd_HH_mm_ss").format(System
                                .currentTimeMillis()) + "P" +".jpeg");

                fop = new FileOutputStream(quickFile.getAbsolutePath());
                // 实例化FileOutputStream，参数是生成路径
                bmp.compress(Bitmap.CompressFormat.JPEG, quickPhoto.photoQulity, fop);
                // 压缩bitmap写进outputStream 参数：输出格式 输出质量 目标OutputStream
                // 格式可以为jpg,png,jpg不能存储透明
                fop.close();
                System.out.println("拍成功");
                if (bmp != null && !bmp.isRecycled()) {
                    bmp.recycle();
                    bmp = null;
                }
                System.gc();
                // android.os.Process.killProcess(android.os.Process.myPid());
                // 关闭流
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("FileNotFoundException");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("IOException");
            }

            break;
        case 1:
            //保存录像
            quickPhoto.isDirExist(quickVideo.STORAGE_LOCATION);
            quickFile = new File(
                    quickVideo.STORAGE_LOCATION,
                    new SimpleDateFormat("yyyyMMdd_HH_mm_ss").format(System.currentTimeMillis()) +"V"+ ".3gp");
            quickVideo.mediaRecorder.setOutputFile(quickFile.getAbsolutePath());
            // 缓冲
            try {
                quickVideo.mediaRecorder.prepare();
            } catch (IllegalStateException e) {
                Log.e("Error", "Error in Line 203");
            } catch (IOException e) {
                Log.e("Error", "Error in Line 206");
            }
            // 开始刻录
            quickVideo.mediaRecorder.start();
            
            while (quickFile == null) {
            }
            break;
            
        case 2:
            //保存录音
            quickPhoto.isDirExist(quickRecord.STORAGE_LOCATION);
            quickFile = new File(quickRecord.STORAGE_LOCATION , new SimpleDateFormat("yyyyMMdd_HH_mm_ss").format(System.currentTimeMillis()) + "R"+".3gp");
            quickRecord.mRecorder = new MediaRecorder();
            quickRecord.mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            quickRecord.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            quickRecord.mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            quickRecord.mRecorder.setOutputFile(quickFile.getAbsolutePath());
            try {
                quickRecord.mRecorder.prepare();
                quickRecord.mRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (quickFile == null) {
            }
            break;
            
        default:
            break;
        }

        } else {
            String noSDCard = activity.getString(R.string.noSDCard);
            Toast.makeText(activity, noSDCard, Toast.LENGTH_LONG)
                    .show();
        }
        return null;
    }

      
    protected void onProgressUpdate(Integer... progress) {
        //在调用publishProgress之后被调用，在ui线程执行  
//        mProgressBar.setProgress(progress[0]);//更新进度条的进度  
     }  

     protected void onPostExecute(String result) {
         //后台任务执行完之后被调用，在ui线程执行  
         //准备发送notification

         if (quickFile != null) {
//             Log.e("ffffffff", "sdsdsdsdsdsdsd");
         notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
         Intent nmintent = OpenSource(quickFile);
         pendingIntent = pendingIntent.getActivity(activity, 0, nmintent, 0);
         
         baseNF = new Notification();
         
         baseNF.icon = R.drawable.app_lancher;
         String str = activity.getString(R.string.ticker_text);
         baseNF.tickerText = str;
         
         baseNF.flags = Notification.FLAG_AUTO_CANCEL;
         str = activity.getString(R.string.save_success);
         String str2 = activity.getString(R.string.save_successful);
         baseNF.setLatestEventInfo(activity, str, str2, pendingIntent);
         
         notificationManager.notify(1 , baseNF);
         }else {
//             Log.e("eeeeeeeeeee", "sdsdsdsdsdsdsd");
        }
     }  
       
     protected void onPreExecute () {
         //在 doInBackground(Params...)之前被调用，在ui线程执行  
//         mProgressBar.setProgress(0);//进度条复位  
     }  
       
     protected void onCancelled () {
         //在ui线程执行  
//         mProgressBar.setProgress(0);//进度条复位  
     }  
      
     
     private Intent OpenSource(File file) {
         String fileSuffixName = FileChooserDialog.getFileNameNoEx(file.toString());
         Log.e("ffffffffff", fileSuffixName);
         Uri mUri = Uri.parse("file://" + file.toString());
         Intent intent = new Intent();
         intent.setAction(Intent.ACTION_VIEW);
         if (fileSuffixName.equals(".jpeg")) {
             intent.setDataAndType(mUri, "image/*");
         } else if (fileSuffixName.equals(".3gp")) {
             intent.setDataAndType(mUri, "video/*");
         }
         return intent;
     }
}  
