package com.solucaoSistemas.AgendaApp;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InsereTarefa extends Activity{
	public ListView lst_usuarios;
	ConectaLocal conectUser;
	String[] USUARIOS;
	private final List<String> selecionados = new ArrayList<String>();
	
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_insere_tarefa);
	        
	        conectUser = new ConectaLocal(getApplicationContext(), "USUARIO");  
	        
			conectUser.setOrder(" ORDER BY NMUSUARIO");
			
			conectUser.setClausula("");
			
			USUARIOS = (MyString.tStringArray(conectUser.select("NMUSUARIO")));
	        
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
			
			
			ArrayAdapter<String> lsvEstadosAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
	            @Override
	            public View getView(int position, View convertView, ViewGroup parent) {
	                // Recuperando o Estado selecionado de acordo com a sua posição no ListView
	            String usuarios = USUARIOS[position];
	 
	                // Se o ConvertView for diferente de null o layout já foi "inflado"
	            View v = convertView;
	 
	                if(v==null) {
	                // "Inflando" o layout do item caso o isso ainda não tenha sido feito
	                LayoutInflater inflater = getLayoutInflater();
	                v = (View) inflater.inflate(R.layout.litview_usuarios, null);
	            }
	 
	            // Recuperando o checkbox
	            CheckBox chk = (CheckBox) v.findViewById(R.id.check_usuario);
	 
	            // Definindo um "valor" para o checkbox
	            chk.setTag(usuarios);
	 
	                /** Definindo uma ação ao clicar no checkbox. Aqui poderiamos armazenar um valor chave
	             * que identifique o objeto selecionado para que o mesmo possa ser, por exemplo, excluído
	             * mais tarde.
	             */
	            chk.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
		            CheckBox chk = (CheckBox) v;
		            String usuario = (String) chk.getTag();
		            if(chk.isChecked()) {
		            	showToast( "Checbox de " + usuario + " marcado!");	
		            	if(!selecionados.contains(usuario))
		                    selecionados.add(usuario);
		            		chk.setChecked(true);
		            } else {
		            	showToast("Checbox de " + usuario + " desmarcado!");
		                if(selecionados.contains(usuario))
		                    selecionados.remove(usuario);
		                	chk.setChecked(false);
		            }
	            }
	        });
	 
	                // Preenche o TextView do layout com o nome do Estado
	            TextView txv = (TextView) v.findViewById(R.id.txtv_nmUsuario);
	            txv.setText(usuarios);
	 
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
	        lst_usuarios.setAdapter(lsvEstadosAdapter);
	    
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.insere_tarefa, menu);
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
		    		return true;		    			
	        }
		            return super.onOptionsItemSelected(item);
	 }
	 
		public void showToast(String texto){		
			Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
		}
		
	
}
