package com.solucaoSistemas.AgendaApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastApp extends BroadcastReceiver {
	private static  String LOG = "teste";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("teste", "BroadcastApp onReceive()");
		intent = new Intent("SERVICO_AGENDA");
		context.startService(intent);
	}

}
