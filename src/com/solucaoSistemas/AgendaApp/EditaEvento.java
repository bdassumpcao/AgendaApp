/**@author Bruno Lopes*/
package com.solucaoSistemas.AgendaApp;


import Utilitarios.MyString;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditaEvento extends Activity{
	private AlertDialog alerta;
	ConectaLocal conectAgenda;
	ConectaLocal conectUser;
	ConectaLocal conectLogAgenda;
	private String cdevento, dataSelecionada;
//	int STATUS=0;
	private EditText etDescricao, etLocal, etObservacao, etData, etHoraInicio, etHoraFim; 
	private TextView tvCdEvento;
	private String campoCdExt, campoDescricao, campoLocal, campoObservacao, campoData, campoHoraInicio, campoHoraFim, campoStatus, campoStatusAtualizado;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().hide();
        
        //para o teclado não aparecer automaticamente
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);    
        
        
        Bundle b = getIntent().getExtras();
        String[] dados = b.getStringArray("key");
        cdevento = dados[0];
        dataSelecionada = dados[1];
        conectAgenda = new ConectaLocal(getApplicationContext(), "AGENDA");
        conectUser = new ConectaLocal(getApplicationContext(), "USUARIO"); 
        conectLogAgenda = new ConectaLocal(getApplicationContext(), "LOGAGENDA");         
        Principal();
   }
    
   public void Principal(){
	    setContentView(R.layout.activity_edita_evento);    	
	    conectAgenda.setClausula("WHERE CDEVENTO = "+cdevento+"");	    
	    
	    campoCdExt = MyString.tString(conectAgenda.select("CDEVENTOEXT"));
	    campoDescricao = MyString.tString(conectAgenda.select("DESCRICAO"));
	    campoLocal = MyString.tString(conectAgenda.select("LOCAL"));
	    campoObservacao = MyString.tString(conectAgenda.select("OBSERVACAO"));
	    campoData = MyString.tString(conectAgenda.select("DATA"));
	    campoData = campoData.replace("\\", "");  
	    campoHoraInicio = MyString.tString(conectAgenda.select("HORAINICIO"));
	    campoHoraFim = MyString.tString(conectAgenda.select("HORAFIM"));
	    campoStatus = MyString.tString(conectAgenda.select("STATUS"));
	    
	    etDescricao = (EditText)findViewById(R.id.campoDescricao);
	    etLocal = (EditText)findViewById(R.id.campoLocal);
	    etObservacao = (EditText)findViewById(R.id.campoObservacao);
	    etData = (EditText)findViewById(R.id.campoData);
	    etHoraInicio = (EditText)findViewById(R.id.campoHoraInicio);
	    etHoraFim = (EditText)findViewById(R.id.campoHoraFim);
	    tvCdEvento = (TextView)findViewById(R.id.campoCdEvento);
	    
	    tvCdEvento.setText(cdevento);
	    
	    if(campoStatus.equals("1")){	    
	    	etDescricao.setFocusable(false);
	    	etDescricao.setClickable(false);
	    	etLocal.setFocusable(false);
	    	etLocal.setClickable(false);	    	
	    	etObservacao.setFocusable(false);	
	    	etObservacao.setClickable(false);
	    	etData.setFocusable(false);	    
	    	etData.setClickable(false);
	    	etHoraInicio.setFocusable(false);
	    	etHoraInicio.setClickable(false);
	    	etHoraFim.setFocusable(false);
	    	etHoraFim.setClickable(false);
	    }
	    etDescricao.setText(campoDescricao);			   
	    etLocal.setText(campoLocal);
	    etObservacao.setText(campoObservacao);		    
	    etData.setText(campoData);		    
	    etHoraInicio.setText(campoHoraInicio);		    
	    etHoraFim.setText(campoHoraFim);	    
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	   MenuInflater inflater = getMenuInflater();
	   inflater.inflate(R.menu.menu_edita_evento, menu);
	   return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle item selection
	   final Intent intent = new Intent(EditaEvento.this, Principal.class);
       switch (item.getItemId()) {
       		case R.id.action_baixar:   
       			if(campoStatus.equals("0")){
       				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//       				builder.setTitle("Titulo");
       		        builder.setMessage("Deseja baixar este evento?");
       		        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
       		        	public void onClick(DialogInterface arg0, int arg1) {
       		        		campoStatusAtualizado = "1";
       		        		salvar(intent, true);
       		        	}
       		        });
       		        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
       		        	public void onClick(DialogInterface arg0, int arg1) {
       		        		
       		        	}
       		        });
       		        alerta = builder.create();
       		        alerta.show();  
       			}
	        	else
		        	showToast("Evento já está baixado!");
       			return true;
       		case R.id.action_Salvar:
       			salvar(intent, false);
       			return true;
       		case R.id.action_Deletar:
       			if(campoStatus.equals("1"))
       				showToast("Evento já está baixado!");
       			else{
       			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
       			builder2.setMessage("Confirma exclusão do evento?");
   		        builder2.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
   		        	public void onClick(DialogInterface arg0, int arg1) {
   		       			deletar(intent);
   		        	}
   		        });
   		        builder2.setNegativeButton("Não", new DialogInterface.OnClickListener() {
   		        	public void onClick(DialogInterface arg0, int arg1) {
   		        		
   		        	}
   		        });
   		        alerta = builder2.create();
   		        alerta.show(); 
       			}
       			return true;
           default:
               	return super.onOptionsItemSelected(item);
       }
   }   
   
   public void salvar(Intent intent,boolean baixar){
	   String comando = "";
	   String CDUSUARIO = ""; 
	   String hi = campoHoraInicio.substring(0, 2) +":";
	   hi += campoHoraInicio.substring(2, 4);
	   String hf = campoHoraFim.substring(0, 2) +":";
	   hf += campoHoraFim.substring(2, 4);
	   

	   
	   if( (!etDescricao.getText().toString().equals(campoDescricao)) ||
			   (!etLocal.getText().toString().equals(campoLocal)) ||
			   (!etObservacao.getText().toString().equals(campoObservacao)) ||
			   (!etData.getText().toString().equals(campoData)) || (!etHoraInicio.getText().toString().equals(hi)) ||
			   (!etHoraFim.getText().toString().equals(hf)) ||
			   (!campoStatus.equals(campoStatusAtualizado)))
	   {	
		   if(valida()){
			   if(baixar){
				   campoStatus = campoStatusAtualizado;
			   }
			   conectUser.setClausula(" WHERE STATUS=1");
			   CDUSUARIO = (MyString.tString(conectUser.select("CDUSUARIO")));
			   conectAgenda.setClausula(" WHERE CDEVENTO = "+cdevento+"");	   
			   
			   comando = "CDUSUARIO="+CDUSUARIO+", " +
			   		"DESCRICAO='"+etDescricao.getText()+"', " +
			   		"LOCAL='"+etLocal.getText()+"', " +
			   		"OBSERVACAO='"+etObservacao.getText()+"'," +
			   		"DATA='"+etData.getText()+"', " +
			   		"HORAINICIO='"+etHoraInicio.getText()+"'," +
			   		"HORAFIM='"+etHoraFim.getText()+"'," +
			   		"STATUS="+campoStatus+" ";
			   conectAgenda.update(comando);	  
			   conectLogAgenda.insert(cdevento+","+campoCdExt+",'U'");// 'U' = update 
			   
			   Bundle b = new Bundle();
			   b.putString("key", etData.getText()+"");
			   intent.putExtras(b);
			   EditaEvento.this.startActivity(intent);
			   EditaEvento.this.finish();   
		   }
		}	
	   else{		
		   Bundle b = new Bundle();
		   b.putString("key", etData.getText()+"");
		   intent.putExtras(b);
		   EditaEvento.this.startActivity(intent);
		   EditaEvento.this.finish();  
	   }
   }   
   
//   public void cancelar(Intent intent){  
//	   Bundle b = new Bundle();
//	   b.putString("key", dataSelecionada);
//	   intent.putExtras(b);
//	   EditaEvento.this.startActivity(intent);
//       EditaEvento.this.finish();   	
//   }
   
   public void deletar(Intent intent){
	   conectAgenda.setClausula(" WHERE CDEVENTO = "+cdevento+"");
	   if(conectAgenda.delete()){
		   campoCdExt = MyString.normalize(campoCdExt);
		   conectLogAgenda.insert(cdevento+","+campoCdExt+",'D'");
		   Bundle b = new Bundle();
		   b.putString("key", dataSelecionada);
		   intent.putExtras(b);
		   EditaEvento.this.startActivity(intent);
		   EditaEvento.this.finish();
//		   showToast(MainActivity.tString(conectLogAgenda.select(" OPERACAO "))); 
	   }
	   else
		   showToast("Não foi possivel excluir evento!");
   }
   
   public boolean valida(){
	   if(InsereEvento.validateNotNull(etDescricao,"Preencha a descrição!"))
		   if(InsereEvento.validateNotNull(etData, "Preencha a data!"))
			   if(InsereEvento.validateNotNullTime(etHoraInicio, "Preencha a hora inicial!"))
				   if(InsereEvento.validateNotNullTime(etHoraFim, "Preencha a hora final!"))
					   if(validaHorarioIgual(etHoraInicio.getText().toString(), etData.getText().toString()))
						   if(InsereEvento.validaIntervalo(etHoraInicio.getText().toString(), etHoraInicio, etHoraFim.getText().toString(), etHoraFim, etData.getText().toString())){
							   return true;
						   }
	   return false;			   
   }
   
   
   public boolean validaHorarioIgual(String horaInicial, String data){
	   conectAgenda.setOrder(" ORDER BY HORAINICIO ");	   
	   conectAgenda.setClausula(" WHERE DATA = '"+data+"' AND HORAINICIO='"+horaInicial+"' ");
	   if(MyString.tString(conectAgenda.select("CDEVENTO")) != ""){		   
		    if(!MyString.tString(conectAgenda.select("CDEVENTO")).equals(cdevento)){
		    	etHoraInicio.setError("Já existe um evento neste horário!!");
		    	etHoraInicio.setFocusable(true);
		    	etHoraInicio.requestFocus();
		    	return false;
		    }
	   }	   
	   return true;
   }

    
	public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   if ((keyCode == KeyEvent.KEYCODE_BACK)) {
		   Intent intent = new Intent(EditaEvento.this, Principal.class);
		   Bundle b = new Bundle();
		   b.putString("key", dataSelecionada);
		   intent.putExtras(b);
		   EditaEvento.this.startActivity(intent);
		   EditaEvento.this.finish();	
	       return true;
	   }
	   return super.onKeyDown(keyCode, event);
	}

}
