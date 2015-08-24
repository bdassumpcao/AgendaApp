package com.solucaoSistemas.AgendaApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Utilitarios.MyString;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class BroadcastReboot extends BroadcastReceiver{
	private static  String LOG = "teste";
	private DatabaseHelper data;
	private SQLiteDatabase db;
	private String tabela = "";
	private String clausula = "";
	private String order = "";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOG, "BroadcastReboot onReceive()");
		
		data = new DatabaseHelper(context);
		db = data.getDatabase();
		
		tabela = "CONFIGURACOES";
        
        //inicia serviço de sincronização
        if(MyString.tString(select("SINCRONIZAR")).equals("1")){
//			intent = new Intent("SINCRONIZACAO_AGENDA");
//			context.sendBroadcast(intent);
//				 
//			context.sendBroadcast(new Intent("SINCRONIZACAO_TAREFA")); 
        	if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent intent1 = new Intent("SINCRONIZACAO_AGENDA");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);               
                
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				c.add(Calendar.SECOND, 3);
				
				AlarmManager alarme = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
				alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 120000, pendingIntent);
				
				
				Intent intent2 = new Intent("SINCRONIZACAO_TAREFA");
				pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
				
				c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				c.add(Calendar.SECOND, 3);
				
				AlarmManager alarme2 = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
				alarme2.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 300000, pendingIntent);
            
        	}

        }				
	}
	
	
	
	public Object select(String campos){
		List<Object> result = new ArrayList<Object>();
		
		Cursor cursor = db.rawQuery("SELECT "+ campos +" FROM "+ tabela +" "+ clausula + order, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			JSONObject obj = new JSONObject();
			
			try{
				obj.put("", cursor.getString(0)+'$');

			}catch(JSONException e){
				
			}
			
			result.add(obj);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return result;
	}
}
