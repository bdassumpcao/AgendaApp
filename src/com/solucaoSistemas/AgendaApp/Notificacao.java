package com.solucaoSistemas.AgendaApp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;



public class Notificacao {
	public Notificacao(){
	}
	
	/**
	 * @author maxissuel
	 * @param context
	 * @param intent
	 * @param icon
	 * @param ticker
	 * @param title
	 * @param message
	 * @param id
	 */
	//para versoes do android anteriores a API 10
	public static void versoesAntigas(Context context, Intent intent, int icon, CharSequence ticker, CharSequence title, CharSequence message, int id){
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification notification = new Notification(icon, ticker, System.currentTimeMillis());
		
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		
		NotificationManager notificationManeger = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManeger.notify(id, notification);
	}
	
	@SuppressLint("NewApi")
	/**
	 * @author maxissuel
	 * @param context
	 * @param intent
	 * @param icon
	 * @param title
	 * @param message
	 * @param id
	 */
	//para versoes acima do API 10
	public static void versoesAtuais(Context context, Intent intent, int icon, CharSequence title, CharSequence message, int id){
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		Notification notification = null;
		
		Notification.Builder builder = new Notification.Builder(context)
		.setContentTitle(title)
		.setContentText(message)
		.setSmallIcon(icon)
		.setContentIntent(pendingIntent);
		
		if(Build.VERSION.SDK_INT == 17){
			notification = builder.build();
		}
		else{
			notification = builder.getNotification();
		}
		
		NotificationManager notificationManeger = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManeger.notify(id, notification);
	}
}
