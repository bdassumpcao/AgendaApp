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
import android.widget.AdapterView.OnItemClickListener;
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
//	private List<String> selecionados = new ArrayList<String>();
	private static  String LOG = "teste";
	TarefasAdapter lsvTarefasAdapter;
	
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
			this.lst_tarefas.setLongClickable(true);
			this.lst_tarefas.setItemsCanFocus(true);
			registerForContextMenu(this.lst_tarefas);
			
			
			
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
			

			lsvTarefasAdapter = new TarefasAdapter(this, R.layout.listview_tarefas, TAREFAS);
	        lst_tarefas.setAdapter(lsvTarefasAdapter);
	        
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
