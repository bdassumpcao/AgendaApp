package com.solucaoSistemas.AgendaApp;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.solucaoSistemas.AgendaApp.ConectaLocal;
import com.solucaoSistemas.AgendaApp.R;

import Utilitarios.MyString;
import Web.Conexao;
import Web.ExecutaWeb;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
 
 
/**
 * @author BRUNO
 *
 */



@SuppressLint("NewApi")
public class MainActivity extends Activity {
	Button bAvanca, opcoes;
	ConectaLocal conectUser;
	Spinner spinnerUsuario;	
	private static  String LOG = "teste";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().hide();
        
		conectUser = new ConectaLocal(getApplicationContext(), "USUARIO");     
        
        try {
			telaInicial();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    
	public void telaInicial() throws InterruptedException{	    	    
		setContentView(R.layout.activity_login);
		
		if(login(true).equals("")){
			conectUser.setClausula(" ORDER BY CDUSUARIO ");
			if(MyString.tString(conectUser.select(" * ")).equals("")){					
				installShortCut();
				
				
				Conexao conexao = new Conexao(this);
				String url = "";
				
				if(conexao.isConected()){
					url = conexao.pegaLink();
					Log.i(LOG, "link:\n"+url);				
					
					String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=user";
					ResponseHandler<String> handler = new BasicResponseHandler();
					HttpClient client = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet("http://"+url+dados);
					Log.i(LOG,"http://"+url+dados);
					Web.ExecutaWeb exec = new Web.ExecutaWeb(handler, client, httpGet);
					
					exec.start();
					
					do{
	//					Log.i(LOG,"sleep");
						Thread.sleep(1000);
					}
					while(exec.respServer.equals(""));				
	
					String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));
	
					if(aux.equals("")){
						Log.i(LOG, "respServer == "+aux);
					}
					else{	
						String[] campos = MyString.montaInsertUsuario(aux);			
						
						for(String i : campos){
							conectUser.insert(i);
						}
						
					}
				}
				else{
					Log.i(LOG, "Sem Conexão");
					showToast("Sem Conexão");
					finish();
				}	
				
			}		
			String[] s;
		    conectUser.setOrder(" ORDER BY NMUSUARIO");
						
			conectUser.setClausula("");
			
			s = (MyString.tStringArray(conectUser.select("NMUSUARIO")));
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, s);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			spinnerUsuario = (Spinner) findViewById(R.id.spinnerUsuario);
			spinnerUsuario.setAdapter(adapter);
			
			bAvanca = (Button) findViewById(R.id.blogin);
			
			bAvanca.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v){
					String user = spinnerUsuario.getSelectedItem().toString();
					
					conectUser.setClausula(" WHERE NMUSUARIO='"+MyString.tiraEspaço(user)+"' ");
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
		
		nome = (MyString.tString(conectUser.select("NMUSUARIO")));
		
		return nome;

	}
	
	
	
}



