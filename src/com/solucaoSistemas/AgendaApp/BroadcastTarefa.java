package com.solucaoSistemas.AgendaApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastTarefa extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("servico_tarefa", "onReceive()");
		intent = new Intent("SERVICO_TAREFA");
		context.startService(intent);
	}

}
