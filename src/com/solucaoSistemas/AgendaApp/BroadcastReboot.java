package com.solucaoSistemas.AgendaApp;

import Utilitarios.MyString;

import com.solucaoSistemas.AgendaApp.Principal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReboot extends BroadcastReceiver{
	private static  String LOG = "teste";
	private ConectaLocal conectConfig;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOG, "BroadcastReboot onReceive()");
		Activity Principal = new Principal();
		conectConfig = new ConectaLocal(Principal.getApplicationContext(), "CONFIGURACOES");
		
		 //inicia serviço de sincronização
        if(MyString.tString(conectConfig.select("SINCRONIZAR")).equals("1")){
			intent = new Intent("SINCRONIZACAO_AGENDA");
			context.sendBroadcast(intent);
			 
			context.sendBroadcast(new Intent("SINCRONIZACAO_TAREFA"));
        }
	}

}
