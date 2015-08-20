/**@author Bruno Lopes*/
package com.solucaoSistemas.AgendaApp;

import Utilitarios.MyString;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Configuracoes extends Activity {
	  ConectaLocal conectConfig;
	  String data;
	  private String senha = "";
	  
	  public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
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
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      getMenuInflater().inflate(R.menu.config, menu);
	      return true;
	  }
	  
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();

	        switch (id){
	    		case R.id.action_sql:	    			
	    			
	    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    			builder.setTitle("Senha");

	    			final EditText edt_senha = new EditText(this);
	    			// Specify the type of edt_senha expected; this, for example, sets the edt_senha as a password, and will mask the text
	    			edt_senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	    			builder.setView(edt_senha);

	    			// Set up the buttons
	    			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
	    			    @Override
	    			    public void onClick(DialogInterface dialog, int which) {
	    			        senha = edt_senha.getText().toString();
	    			        if(senha.equals("123")){
	    			        	startActivity(new Intent(Configuracoes.this, Sql.class));
	    			        }
	    			        else
	    			        	edt_senha.setError("Senha incorreta!");
	    			    }
	    			});
	    			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    			    @Override
	    			    public void onClick(DialogInterface dialog, int which) {
	    			        dialog.cancel();
	    			    }
	    			});

	    			builder.show();
	    			return true;
	        }
	    	return super.onOptionsItemSelected(item);
	    }
	  
	  public  void startService(){
			Intent intent = new Intent("SERVICO_AGENDA");
			startService(intent);
			
			startService(new Intent("SERVICO_TAREFA"));
		}

	  
	  public void stopService(){
		  	Intent intent = new Intent("SINCRONIZACAO_AGENDA");
			PendingIntent p = PendingIntent.getBroadcast(Configuracoes.this, 0, intent, 0);			
			AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarme.cancel(p);
			
			Intent intent1 = new Intent("SINCRONIZACAO_TAREFA");
			PendingIntent p1 = PendingIntent.getBroadcast(Configuracoes.this, 0, intent1, 0);			
			AlarmManager alarme1 = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarme1.cancel(p1);
			
			stopService(new Intent("SERVICO_AGENDA"));
			
			stopService(new Intent("SERVICO_TAREFA"));
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
