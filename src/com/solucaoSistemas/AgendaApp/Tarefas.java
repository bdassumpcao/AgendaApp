package com.solucaoSistemas.AgendaApp;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Tarefas extends Activity{
	
	private ConectaLocal conectUser;
	private ConectaLocal conectTarefa;
	private String[] TAREFAS;
	private ListView lst_tarefas;
	private List<String> selecionados = new ArrayList<String>();
	private static  String LOG = "teste";
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_tarefas);
   
			getActionBar().setDisplayShowHomeEnabled(false);
//			getActionBar().hide();
	        
	        conectUser = new ConectaLocal(getApplicationContext(), "USUARIO"); 
	        conectTarefa = new ConectaLocal(getApplicationContext(), "TAREFA");
	        
			conectTarefa.setOrder(" ORDER BY CDTAREFA");
			
			conectTarefa.setClausula("");
			
			TAREFAS = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
	        
			
			this.lst_tarefas = (ListView)findViewById(R.id.lst_tarefas);
			lst_tarefas.setLongClickable(true);
			lst_tarefas.setItemsCanFocus(true);
			registerForContextMenu(lst_tarefas);
			
			
			
			lst_tarefas.setOnTouchListener(new ListView.OnTouchListener() {
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
			
			ArrayAdapter<String> lsvTarefasAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {	
				@Override
	            public View getView(final int position, View convertView, ViewGroup parent) {
		                // Recuperando o Estado selecionado de acordo com a sua posição no ListView
		            final String cdTarefa = TAREFAS[position];
		 
		                // Se o ConvertView for diferente de null o layout já foi "inflado"
		            View v = convertView;
		 
		                if(v==null) {
			                // "Inflando" o layout do item caso o isso ainda não tenha sido feito
			                LayoutInflater inflater = getLayoutInflater();
			                v = (View) inflater.inflate(R.layout.listview_tarefas, null);
		                }

		            // Recuperando o checkbox
		            final CheckBox check = (CheckBox) v.findViewById(R.id.check_tarefa);

		            conectTarefa.setClausula(" WHERE CDTAREFA="+cdTarefa);
		            String descricao = MyString.tString(conectTarefa.select(" NMDESCRICAO "));
		            String responsavel = getNmUsuario(MyString.tString(conectTarefa.select(" CDRESPONSAVEL ")));
		            String destinatarios = getNmDestinatarios(MyString.tString3(conectTarefa.select(" NMDESTINATARIOS ")));
		            String status = MyString.tString(conectTarefa.select(" CDSTATUS "));
		            
		            if(status.equals("1")) {
		                check.setChecked(true);
		            } else {
		                check.setChecked(false);
		            }
		            
		            // Preenche o TextView do layout com o nome do Estado
		            TextView txtv_Descricao = (TextView) v.findViewById(R.id.txtv_Descricao);
		            TextView txtv_nmResponsavel = (TextView) v.findViewById(R.id.txtv_nmResponsavel);
		            TextView txtv_nmDestinatario = (TextView) v.findViewById(R.id.txtv_nmDestinatarios);
		            txtv_Descricao.setText(descricao.trim());
		            txtv_nmResponsavel.setText(responsavel.trim());
		            txtv_nmDestinatario.setText(destinatarios.trim());
		            
		            check.setOnClickListener(new View.OnClickListener() {
			            @Override
			            public void onClick(View v) {
				            CheckBox cb = (CheckBox) v.findViewById(R.id.check_tarefa);
				            conectTarefa.setClausula(" WHERE CDTAREFA="+cdTarefa);
				            if (cb.isChecked()) {
				            	conectTarefa.update(" CDSTATUS=1 ");
				                selecionados.add(cdTarefa);
				            } else if (!cb.isChecked()) {
				            	conectTarefa.update(" CDSTATUS=0 ");
				                selecionados.remove(cdTarefa);
				            }
		
			            }
		            });
		            if(selecionados.contains(cdTarefa)) {
		                check.setChecked(true);
		            } else {
		                check.setChecked(false);
		            }
		 
		            return v;
	            }
	 
				public String getNmUsuario(String cd){
					conectUser.setClausula(" WHERE CDUSUARIO="+cd);
					String nmusuario = MyString.tString(conectUser.select(" NMUSUARIO "));
					return nmusuario;	
				}
				
				public String getNmDestinatarios(String string){
					String destinatarios = "";
					String nm = "";
					char[] aux = new char[string.length()];
					int x = 0;
				
					for(int i = 0; i < string.length(); i++){
						aux[i] = string.charAt(i);
						if(aux[i] == ','){
							x++;
						}
					}
					
					int j = 0;
					String[] re = new String[x+1];
					
					for(int i = 0; i < aux.length; i++){
						if(aux[i] == ','){
							re[j] = nm;
							nm = "";
							j++;
						}
						else if(i == aux.length-1){
							nm += aux[i];
							re[j] = nm;
							nm = "";
							j++;
						}
						else{
							nm += aux[i];
						}
					}
					
					for(int i=0; i<re.length; i++){						
						if( i == re.length-1)
							destinatarios += getNmUsuario(re[i]);
						else
							destinatarios += getNmUsuario(re[i])+",";
					}
					return destinatarios;
				}
				
	            @Override
	            public long getItemId(int position) {
	                return position;
	            }
	 
	            @Override
	            public int getCount() {
	                return TAREFAS.length;
	            }
	        };
	        lst_tarefas.setAdapter(lsvTarefasAdapter);
	        
	        
	        lst_tarefas.setOnLongClickListener(new OnLongClickListener() {

	            @Override
	            public boolean onLongClick(View v) {
	                ((Activity)getApplicationContext()).openContextMenu(v);
	                return true;
	            }
	        });
	        
	        

	 }
	 
	 
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_tarefas, menu);
	}
	 
	@Override
	public boolean onContextItemSelected(MenuItem item) {		
	    switch (item.getItemId()) {
	        case R.id.action_removerTarefa:
	        	//Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show();
	        	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
	        	
	        	showToast(info.position+"");
	        	

	        	 
	            return true;
	        
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.menu_tarefas, menu);
	     return true;
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();

	        switch (id){
	    		case R.id.action_novaTarefa:
		    		Intent intent = new Intent(Tarefas.this, InsereTarefa.class);
		    		Tarefas.this.startActivity(intent);
		    		return true;		    		
	    		case R.id.action_eventos:
		    		Intent intent2 = new Intent(Tarefas.this, Principal.class);
		    		Tarefas.this.startActivity(intent2);
		    		Tarefas.this.finish();
		    		return true;	
	        }
		            return super.onOptionsItemSelected(item);
	 }
	 
	public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
	}
		
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    		Tarefas.this.startActivity(new Intent(Tarefas.this, Principal.class));
	    		Tarefas.this.finish();
		        return true;
		    }
		    return super.onKeyDown(keyCode, event);
		}
	 
}
