/**@author Bruno Lopes*/
package com.solucaoSistemas.AgendaApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.solucaoSistemas.AgendaApp.R;

@SuppressLint("SimpleDateFormat")
public class InsereEvento extends Activity{
	ConectaLocal conectEvento;
	ConectaLocal conectUser;
	EditText campo, etDescricao, etLocal, etObservacao, etData, etHoraInicio, etHoraFim; 
	String campoDescricao, campoLocal, campoObservacao, campoData, campoHoraInicio, campoHoraFim;
	String data;
	String cdusuario = "";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //para o teclado não aparecer automaticamente
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
        
        Bundle b;
        if(getIntent().getExtras() != null){
        	b = getIntent().getExtras();
        	data = b.getString("key");
//        	data = data.replace( "/" , "");     
        }
        else{
      	   SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
      	   data = format.format(new Date()); 
      	   data = data.replace("/", "");
         }
        
        conectEvento = new ConectaLocal(getApplicationContext(), "AGENDA");
        conectUser = new ConectaLocal(getApplicationContext(), "USUARIO");  
        Principal();
   }
    
   public void Principal(){
	    setContentView(R.layout.activity_evento); 
	   	campo = (EditText)findViewById(R.id.campoData);        	
    	campo.setText(data);
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
   	MenuInflater inflater = getMenuInflater();
   	inflater.inflate(R.menu.insere_evento, menu);
       return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle item selection
	   Intent intent = new Intent(InsereEvento.this, Principal.class);
       switch (item.getItemId()) {
           case R.id.action_Salvar:
               salvar(intent);
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }
   }
   	
   public void salvar(Intent intent){
	   String comando = "";
	   String cdevento = null;
	   int status = 0;
					
	   conectUser.setClausula(" WHERE STATUS=1");
		
	   cdusuario = (MainActivity.tString(conectUser.select("CDUSUARIO")));
	   etDescricao = (EditText)findViewById(R.id.campoDescricao);
	   campoDescricao = etDescricao.getText().toString();
	   etLocal = (EditText)findViewById(R.id.campoLocal);
	   campoLocal = etLocal.getText().toString();
	   etObservacao = (EditText)findViewById(R.id.campoObservacao);
	   campoObservacao = etObservacao.getText().toString();
	   etData = (EditText)findViewById(R.id.campoData);
	   campoData = etData.getText().toString();
	   etHoraInicio = (EditText)findViewById(R.id.campoHoraInicio);
	   campoHoraInicio = etHoraInicio.getText().toString();
	   etHoraFim = (EditText)findViewById(R.id.campoHoraFim);
	   campoHoraFim = etHoraFim.getText().toString();
	   
	   comando = ""+cdevento+",null,"+cdusuario+",'"+campoDescricao+"','"+campoLocal+"','"+campoObservacao+"','"+campoData+"','"+campoHoraInicio+"','"+campoHoraFim+"',"+status+" ";
	   
	   if(validateNotNull(etDescricao,"Preencha a descrição!")){
		   if(validateNotNullDate(etData, "Preencha a data!")){
			   if(validateNotNullTime(etHoraInicio, "Preencha a hora inicial!")){
				   if(validateNotNullTime(etHoraFim, "Preencha a hora final!")){
					   if(validaHorarioIgual(campoHoraInicio, campoData)){
						   if(validaIntervalo(campoHoraInicio, etHoraInicio, campoHoraFim, etHoraFim, campoData)){
							   conectEvento.insert(comando);						   
			   				   Bundle b = new Bundle();
			   				   b.putString("key", campoData);
			   				   intent.putExtras(b);
			   				   InsereEvento.this.startActivity(intent);
			   				   InsereEvento.this.finish();   
			   			   }
					   }
				    }
			    }
		    }
	    }
   }    

//   public void cancelar(Intent intent){  
//	   Bundle b = new Bundle();
//	   String aux = data.substring(0, 2) +"/";
//	   aux += data.substring(2, 4)+"/";
//	   aux += data.substring(4, 8);
//	   b.putString("key", aux);
//	   intent.putExtras(b);
//       InsereEvento.this.startActivity(intent);
//       InsereEvento.this.finish();   	
//   }
   
   public static boolean validaIntervalo(String horaInicial,EditText etHoraInicio, String horaFinal, EditText etHoraFim, String data){
	   int hi = Integer.parseInt(horaInicial.substring(0,2));
	   int hf = Integer.parseInt(horaFinal.substring(0, 2));
	   int mi = Integer.parseInt(horaInicial.substring(3,5));
	   int mf = Integer.parseInt(horaFinal.substring(3, 5));	 	   
	   
	   if(hf<hi){
		    etHoraFim.setError("Hora final não pode ser menor que hora inicial do evento!!");
		    etHoraFim.setFocusable(true);
		    etHoraFim.requestFocus();
		   return false;
	   }
	   
	   if(horaInicial.equals(horaFinal)){
		    etHoraFim.setError("Hora final deve ser diferente da inicial!!");
		    etHoraFim.setFocusable(true);
		    etHoraFim.requestFocus();
		    return false;
	   }
	   
	   if(hf == hi){
		   if(mf<mi){
			   etHoraFim.setError("Hora final não pode ser menor que hora inicial do evento!!");
			   etHoraFim.setFocusable(true);
			   etHoraFim.requestFocus();
			   return false;
		   }
		   
		   if( (mf-mi) < 20){
			   etHoraFim.setError("Duração do evento não pode ser menor que 20 minutos!!");
			   etHoraFim.setFocusable(true);
			   etHoraFim.requestFocus();
			   return false;
		   }
	   }   
	   
	   return true;	   
    }
   
   public static boolean validateNotNull(View pView, String pMessage) {
		if (pView instanceof EditText) {
			EditText edText = (EditText) pView;
		    Editable text = edText.getText();
		    if (text != null) {
		    	String strText = text.toString();
		    	if (!TextUtils.isEmpty(strText)) {
		    		return true;
		    	}
		    }
		    // em qualquer outra condição é gerado um erro
		    edText.setError(pMessage);
		    edText.setFocusable(true);
		    edText.requestFocus();
		    return false;
		}
		 	return false;
	}
	
   public boolean validaHorarioIgual(String horaInicial, String data){
	   conectEvento.setOrder(" ORDER BY HORAINICIO");	   
	   conectEvento.setClausula("WHERE DATA = '"+data+"' AND HORAINICIO='"+horaInicial+"'");
	   if(MainActivity.tString(conectEvento.select("HORAINICIO")) != ""){
		    etHoraInicio.setError("Já existe um evento neste horário!!");
		    etHoraInicio.setFocusable(true);
		    etHoraInicio.requestFocus();
		   return false;
	   }
	   return true;
   }
   
	public static boolean validateNotNullTime(View pView, String pMessage) {
		if (pView instanceof EditText) {
			EditText edText = (EditText) pView;
		    String text = edText.getText().toString();		    
		    
		    
		    if(!text.equals("__:__")) 
		    	if(text.indexOf("_") < 0){
		    		int min1 = Integer.parseInt(text.substring(3, 4));
		    		if(min1 < 6){
		    			int h1 = Integer.parseInt(text.substring(0, 2));
		    			if(h1<=23){
		    				String strText = text.toString();
		    				if (!TextUtils.isEmpty(strText)) {
		    					return true;
		    				}
		    			}
		    			pMessage = "Horário inválido!";
		    		}
		    	}
		    // em qualquer outra condição é gerado um erro
		    edText.setError(pMessage);
		    edText.setFocusable(true);
		    edText.requestFocus();
		    return false;
		}		
		return false;
	}
	
	public static boolean validateNotNullDate(View pView, String pMessage) {
		if (pView instanceof EditText) {
			EditText edText = (EditText) pView;
		    String text = edText.getText().toString();
		    
		    if (!text.equals("__/__/____")) {
		    	int d = Integer.parseInt(text.substring(0, 2));
		    	int m = Integer.parseInt(text.substring(3, 5));
		    	if(m<=12 && d<=31){
		    		String strText = text.toString();
		    		if (!TextUtils.isEmpty(strText)) {
		    			return true;
		    		}
		    	}
		    	pMessage = "Data inválida!";
		    }
		    // em qualquer outra condição é gerado um erro
		    edText.setError(pMessage);
		    edText.setFocusable(true);
		    edText.requestFocus();
		    return false;
		}	
		 	return false;
	}
   
	public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				Intent intent = new Intent(InsereEvento.this, Principal.class);
				   Bundle b = new Bundle();
				   String aux = data.substring(0, 2) +"/";
				   aux += data.substring(2, 4)+"/";
				   aux += data.substring(4, 8);
				   b.putString("key", aux);
				   intent.putExtras(b);
				InsereEvento.this.startActivity(intent);
				InsereEvento.this.finish();	
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	 }

}
