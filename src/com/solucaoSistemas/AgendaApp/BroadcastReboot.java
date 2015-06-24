package com.solucaoSistemas.AgendaApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReboot extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		 intent = new Intent("SINCRONIZACAO_AGENDA");
		 context.sendBroadcast(intent);
		 
		 context.sendBroadcast(new Intent("SINCRONIZACAO_TAREFA"));
	}

}
