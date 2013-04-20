package com.uniquestudio.quick;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;

public class photoTaker extends AppWidgetProvider{
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		final int N = appWidgetIds.length;
		
		for(int i = 0; i < N; i++){
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, quickPhoto.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.phototaker);
//			LayoutInflater inflater = (LayoutInflater)   
//					 context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
//					         View layout = inflater.inflate(R.layout.phototaker,null);  
			views.setOnClickPendingIntent(R.id.photo_small, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}
