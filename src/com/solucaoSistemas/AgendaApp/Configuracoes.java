/**@author Bruno Lopes*/
package com.solucaoSistemas.AgendaApp;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class Configuracoes extends Activity {
	  ConectaLocal conectConfig;
	  String data;
	  
	  public void onCreate(Bundle icicle) {
		    super.onCreate(icicle);
		    setContentView(R.layout.activity_configuracoes);
		    
			getActionBar().setDisplayShowHomeEnabled(false);
//			getActionBar().hide();
		    
		    conectConfig = new ConectaLocal(getApplicationContext(), "CONFIGURACOES");		
		    
		    final CheckBox sincronizar = (CheckBox)findViewById(R.id.sincronizar);
		    final CheckBox baixado = (CheckBox)findViewById(R.id.baixados);
		    
		    if(MyString.tString(conectConfig.select("SINCRONIZAR")).equals("1")){
		    	sincronizar.setChecked(true);
		    }
		    else {
				sincronizar.setChecked(false);
			}
		    
		    if(MyString.tString(conectConfig.select("BAIXADO")).equals("1")){
		    	baixado.setChecked(true);
		    }
		    else {
				baixado.setChecked(false);
			}
		    
		    Button salvar = (Button)findViewById(R.id.btSalvar);
		    
		    salvar.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(sincronizar.isChecked()){
						conectConfig.update("SINCRONIZAR=1");
						boolean alarmeAtivo = (PendingIntent.getBroadcast(Configuracoes.this, 0, new Intent("SINCRONIZACAO_AGENDA"), PendingIntent.FLAG_NO_CREATE) == null);
						
						if(alarmeAtivo){
							Log.i("teste", "Novo alarme");
							
							Intent intent = new Intent("SINCRONIZACAO_AGENDA");
							PendingIntent p = PendingIntent.getBroadcast(Configuracoes.this, 0, intent, 0);
							
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(System.currentTimeMillis());
							c.add(Calendar.SECOND, 3);
							
							AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
							alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 120000, p);
						}
					
					}
					else if(!sincronizar.isChecked()){
						conectConfig.update("SINCRONIZAR=0");
						stopService();						
					}
					if(baixado.isChecked()){						
						conectConfig.update("BAIXADO=1");
					}
					else if(!baixado.isChecked()){
						conectConfig.update("BAIXADO=0");
					}
					
					Intent intent = new Intent(Configuracoes.this, Principal.class);
					Configuracoes.this.startActivity(intent);
					Configuracoes.this.finish();					
				}
			});		    
		    
	  }
	  
	  public  void startService(){
			Intent intent = new Intent("SERVICO_AGENDA");
			startService(intent);
		}

	  
	  public void stopService(){
		  	Intent intent = new Intent("SINCRONIZACAO_AGENDA");
			PendingIntent p = PendingIntent.getBroadcast(Configuracoes.this, 0, intent, 0);			
			AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarme.cancel(p);
		}
	  
	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				Intent intent = new Intent(Configuracoes.this, Principal.class);
				Configuracoes.this.startActivity(intent);
				Configuracoes.this.finish();	
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }

	  
	  
}
