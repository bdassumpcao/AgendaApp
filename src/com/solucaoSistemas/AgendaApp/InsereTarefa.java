package com.solucaoSistemas.AgendaApp;


import java.util.ArrayList;
import java.util.List;

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
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
	        
			conectUser.setOrder(" ORDER BY NMUSUARIO");
			
			conectUser.setClausula("");
			
			USUARIOS = (MyString.tStringArray(conectUser.select("NMUSUARIO")));
			
			usuarioAtivo = getUsuarioAtivo();
	        
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
				@Override
	            public View getView(final int position, View convertView, ViewGroup parent) {
	                // Recuperando o Estado selecionado de acordo com a sua posição no ListView
	            final String usuario = USUARIOS[position];
	 
	                // Se o ConvertView for diferente de null o layout já foi "inflado"
	            View v = convertView;
	 
	                if(v==null) {
		                // "Inflando" o layout do item caso o isso ainda não tenha sido feito
		                LayoutInflater inflater = getLayoutInflater();
		                v = (View) inflater.inflate(R.layout.listview_usuarios, null);
	                }
	                

	 
	            // Recuperando o checkbox
	            final CheckBox check = (CheckBox) v.findViewById(R.id.check_usuario);
	 	 
	                /** Definindo uma ação ao clicar no checkbox. Aqui poderiamos armazenar um valor chave
	             * que identifique o objeto selecionado para que o mesmo possa ser, por exemplo, excluído
	             * mais tarde.
	             */
	            check.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
		            CheckBox cb = (CheckBox) v.findViewById(R.id.check_usuario);
		            if (cb.isChecked()) {
		                selecionados.add(usuario);
		            } else if (!cb.isChecked()) {
		                selecionados.remove(usuario);
		            }

	            }
	        });
	            if(selecionados.contains(usuario)) {
	                check.setChecked(true);
	            } else {
	                check.setChecked(false);
	            }
	 
	                // Preenche o TextView do layout com o nome do Estado
	            TextView txv = (TextView) v.findViewById(R.id.txtv_nmUsuario);
	            txv.setText(usuario.trim());
	 
	            return v;
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
		    		
		    		String cdDestinatarios = "";
		    		for(int i=0; i<selecionados.size() ; i++){
		    			if( i == selecionados.size()-1)
		    				cdDestinatarios += getCodUsuario(selecionados.get(i));
		    			else		    				
		    				cdDestinatarios += getCodUsuario(selecionados.get(i))+",";
		    		}
		    		
		    		conectTarefa.setClausula("");
		    		conectTarefa.insert("null,'"+edt_desc.getText().toString()+"',0,'"+cdDestinatarios+"',"+usuarioAtivo+",null");
		    		
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
	
}
