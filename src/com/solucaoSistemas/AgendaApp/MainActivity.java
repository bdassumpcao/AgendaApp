package com.solucaoSistemas.AgendaApp;

import com.solucaoSistemas.AgendaApp.ConectaLocal;
import com.solucaoSistemas.AgendaApp.R;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
 
 
public class MainActivity extends Activity {
	Button bAvanca, opcoes;
	ConectaLocal conectUser;
	Spinner spinnerUsuario;	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		conectUser = new ConectaLocal(getApplicationContext(), "USUARIO");     
        
        telaInicial(); 
    }
    
    
	public void telaInicial(){	    	    
		setContentView(R.layout.activity_login);
		
		if(login(true).equals("")){
			installShortCut();
			String[] s;
			
			conectUser.setOrder(" ORDER BY NMUSUARIO");
			
			conectUser.setClausula("");
			
			s = (tStringArray(conectUser.select("NMUSUARIO")));
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, s);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			spinnerUsuario = (Spinner) findViewById(R.id.spinnerUsuario);
			spinnerUsuario.setAdapter(adapter);
			
			bAvanca = (Button) findViewById(R.id.blogin);
			
			bAvanca.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v){
					String user = spinnerUsuario.getSelectedItem().toString();
					
					conectUser.setClausula(" WHERE NMUSUARIO='"+tiraEspaço(user)+"' ");
					conectUser.update(" STATUS=1 ");
					telaPrincipal();
				}
			});	
			
		}
		else{
			telaPrincipal();
		}
		
	}
	
	public void telaPrincipal(){		
        Intent intent = new Intent(MainActivity.this, Principal.class);  
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();	
	}
		
	public void installShortCut(){
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isAppInstalled = appPreferences.getBoolean("isAppInstalled",false);

        if(isAppInstalled==false){

            Intent shortcutIntent = new Intent(getApplicationContext(),MainActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Agenda");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

            getApplicationContext().sendBroadcast(intent);
            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean("isAppInstalled", true);
            editor.commit();
        }
    }

    
	public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
	}
      
	
	public String login(boolean a){
		String nome = "";	
		
		conectUser.setClausula(" WHERE STATUS=1");
		
		nome = (tString(conectUser.select("NMUSUARIO")));
		
		return nome;

	}
	
	
	public static String tString(Object string){
		
		String resul = string.toString();
		char[] aux = new char[resul.length()];
		
		for(int i = 0; i < resul.length(); i++){
			aux[i] = resul.charAt(i);
		}
		
		resul = "";
		
		for(int i = 0; i < aux.length; i++){
			
			if(aux[i] == '$' || aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ',' || aux[i] == ':'){
				
			}
			else{
				resul += aux[i];
			}
		}	
		return resul;
	}

	public static String[] tStringArray(Object string){
		
		String resul = string.toString();
		char[] aux = new char[resul.length()];
		
		for(int i = 0; i < resul.length(); i++){
			aux[i] = resul.charAt(i);
		}
		
		resul = "";
		
		int j = 0;
		for(int i = 0; i < aux.length; i++){
			if(aux[i] == '$'){
				j++;
			}
		}
		
		String[] a = new String[j];
		j = 0;
		for(int i = 0; i < aux.length; i++){
			if(aux[i] == '$'){
				a[j] = resul;
				j++;
				resul = "";
			}
			
			else if(aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ',' || aux[i] == ':'){
				
			}
			
			else{
				resul += aux[i];
			}
		}		
		return a;
	}
	
	public static String tiraEspaço(String string){
		String resp = "";

		for(int i = 0; i < string.length(); i++){
			if(string.charAt(0) == ' ' && i == 0){
			}
			else{
				resp += string.charAt(i);
			}
		}		
		return resp;
	}		
}



