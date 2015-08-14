package com.solucaoSistemas.AgendaApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReboot extends BroadcastReceiver{
	private static  String LOG = "teste";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOG, "BroadcastReboot onReceive()");
		 intent = new Intent("SINCRONIZACAO_AGENDA");
		 context.sendBroadcast(intent);
		 
		 context.sendBroadcast(new Intent("SINCRONIZACAO_TAREFA"));
	}

}
