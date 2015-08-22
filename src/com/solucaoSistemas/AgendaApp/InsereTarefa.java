package com.solucaoSistemas.AgendaApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import Utilitarios.MyString;
import Web.Conexao;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class InsereTarefa extends Activity{		
	private ConectaLocal conectUser;
	private ConectaLocal conectTarefa;
	private String[] USUARIOS;
	private String usuarioAtivo;
	private static  String LOG = "teste";
	private EditText edt_desc;
	private ListView lst_usuarios;
	private List<String> selecionados = new ArrayList<String>();
	
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_insere_tarefa);
	        
			getActionBar().setDisplayShowHomeEnabled(false);
//			getActionBar().hide();
	        
	        conectUser = new ConectaLocal(getApplicationContext(), "USUARIO"); 
	        conectTarefa = new ConectaLocal(getApplicationContext(), "TAREFA");
	        
	        usuarioAtivo = getUsuarioAtivo();
			conectUser.setOrder(" ORDER BY NMUSUARIO");			
			conectUser.setClausula(" WHERE CDUSUARIO!="+usuarioAtivo);			
			USUARIOS = (MyString.tStringArray(conectUser.select("NMUSUARIO")));			
			
	        
			this.edt_desc = (EditText)findViewById(R.id.edt_desc);
			this.lst_usuarios = (ListView)findViewById(R.id.lst_usuarios);
			lst_usuarios.setItemsCanFocus(true);
			registerForContextMenu(lst_usuarios);

			lst_usuarios.setOnTouchListener(new ListView.OnTouchListener() {
		        @Override
		        public boolean onTouch(View v, MotionEvent event) {
		            int action = event.getAction();
		            switch (action) {
		            case MotionEvent.ACTION_DOWN:
		                // Disallow ScrollView to intercept touch events.
		                v.getParent().requestDisallowInterceptTouchEvent(true);
		                break;

		            case MotionEvent.ACTION_UP:
		                // Allow ScrollView to intercept touch events.
		                v.getParent().requestDisallowInterceptTouchEvent(false);
		                break;
		            }
		            // Handle ListView touch events.
		            v.onTouchEvent(event);
		            return true;
		        }
		    });
			
			selecionados.clear();
			
			ArrayAdapter<String> lsvUsuariosAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {	
				@SuppressLint("InflateParams")
				@Override
	            public View getView(final int position, View convertView, ViewGroup parent) {
		            final String usuario = USUARIOS[position];
		 
		            View v = convertView;	 
		               
		            if(v==null) {
			            LayoutInflater inflater = getLayoutInflater();
			            v = (View) inflater.inflate(R.layout.listview_usuarios, null);
		            }	      
		                		             
		            final CheckBox check = (CheckBox) v.findViewById(R.id.check_usuario);
		            check.setText(usuario.trim());
	 	 
		            
	                /** Definindo uma ação ao clicar no checkbox.
		             */
		            check.setOnClickListener(new View.OnClickListener() {
			            @Override
			            public void onClick(View view) {
				            selecionados(view, usuario);	
		            	}
		            });
		            
		            if(selecionados.contains(usuario)) {
		                check.setChecked(true);
		            } else {
		                check.setChecked(false);
		            } 
		            
		            return v;
	            }
				
				public void selecionados(View v, String usuario){
		            CheckBox cb = (CheckBox) v.findViewById(R.id.check_usuario);
		            
		            for(int i=0;i<selecionados.size();i++){
		            }
		            
		            if (cb.isChecked()) {
		            	if(!selecionados.contains(usuario)){
		            		selecionados.add(usuario);
		            	}
		            } else if (!cb.isChecked()) {
		            	if(selecionados.contains(usuario)){
		            		selecionados.remove(usuario);
		            	}
		            }	

		            notifyDataSetChanged();
				}
				
	            @Override
	            public long getItemId(int position) {
	                return position;
	            }
	 
	            @Override
	            public int getCount() {
	                return USUARIOS.length;
	            }
	        };
	        lst_usuarios.setAdapter(lsvUsuariosAdapter);   
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.menu_insere_tarefa, menu);
	     return true;
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();

	        switch (id){
	    		case R.id.action_Salvar:
		    		Intent intent = new Intent(InsereTarefa.this, Tarefas.class);
		    		InsereTarefa.this.startActivity(intent);
		    		InsereTarefa.this.finish();
		    		
		    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    		Date data = new Date();
		    		Calendar c = Calendar.getInstance();
		    		c.setTime(data);
		    		Date dataAtual = c.getTime();
		    		data.getTime();
		    		
		    		final String actualData = dateFormat.format(dataAtual);
		    		
		    		String cdDestinatario = "";
		    		int referencia = (pegaUltimaRef()+1);
		    		if(selecionados.size() == 0)
		    			conectTarefa.insert("null,'"+edt_desc.getText().toString()+"','A','',"+usuarioAtivo+","+referencia+",'"+actualData+"',null,null");		    		
		    		else{
			    		for(int i=0; i<selecionados.size() ; i++){
			    			cdDestinatario = getCodUsuario(selecionados.get(i));
			    			conectTarefa.insert("null,'"+edt_desc.getText().toString()+"','A',"+cdDestinatario+","+usuarioAtivo+","+referencia+",'"+actualData+"',null,null");
			    		}
		    		}

		    		return true;
	    		case R.id.action_AtualizarContatos:
	    			
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
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						while(exec.respServer.equals(""));	
						if(exec.respServer.contains("Erro")){
							Log.i(LOG, "respServer == "+exec.respServer);
							showToast("Erro: \nServidor não responde.");	
							return false;
						}
						String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));
		
						if(aux.equals("")){
							Log.i(LOG, "respServer == "+aux);
						}
						else{	
							String[] campos = MyString.montaInsertUsuario(aux);			
							
							
							conectUser.setClausula("");
							conectUser.delete();
							
							for(String i : campos){
								conectUser.insert(i);
							}
							
							conectUser.setClausula(" WHERE CDUSUARIO="+usuarioAtivo);
							conectUser.update(" STATUS=1 ");
							
						}						
						
						Intent i = getIntent();
						this.finish();
						startActivity(i);
					}
					else{
						Log.i(LOG, "Sem Conexão");
						showToast("Sem Conexão");
						return false;
					}					
	    			
	    			return true;
	        }
		            return super.onOptionsItemSelected(item);
	 }
	 
	 public String getCodUsuario(String nome){
		 String cod="";
		 conectUser.setClausula(" WHERE NMUSUARIO='"+nome.trim()+"'");
		 cod = MyString.tString(conectUser.select(" CDUSUARIO "));
		 return cod;
	 }
	 
	 public String getUsuarioAtivo(){
		 conectUser.setClausula(" WHERE STATUS=1");
		 String cd = MyString.tString(conectUser.select(" CDUSUARIO "));
		 return cd;
	 }
	 
	 
	public int pegaUltimaRef(){
		conectTarefa.setClausula(" WHERE CDRESPONSAVEL="+usuarioAtivo);
		if(MyString.tString(conectTarefa.select(" MAX(CDREFERENCIA) ")).equals("null"))
			return 0;
		else return Integer.parseInt(MyString.tString(conectTarefa.select(" MAX(CDREFERENCIA) ")));
	}
	
	public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
    		InsereTarefa.this.startActivity(new Intent(InsereTarefa.this, Tarefas.class));
    		InsereTarefa.this.finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		conectTarefa.close();
		conectUser.close();
	}
}
