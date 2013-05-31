package com.uniquestudio.quick;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class widgetTaker extends AppWidgetProvider{
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        final int N = appWidgetIds.length;
        
        for(int i = 0; i < N; i++){
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_taker);
            //设置点击响应
            //photo
            Intent intent_photo = new Intent(context, quickPhoto.class);
            PendingIntent pi_photo = PendingIntent.getActivity(context, 0, intent_photo, 0);
            views.setOnClickPendingIntent(R.id.photo_s, pi_photo);
            
            //video
            Intent intent_video = new Intent(context, quickVideo.class);
            PendingIntent pi_video = PendingIntent.getActivity(context, 0, intent_video, 0);
            views.setOnClickPendingIntent(R.id.video_s, pi_video);
            
            //record
            Intent intent_record = new Intent(context, quickRecord.class);
            PendingIntent pi_record = PendingIntent.getActivity(context, 0, intent_record, 0);
            views.setOnClickPendingIntent(R.id.record_s, pi_record);
            
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
