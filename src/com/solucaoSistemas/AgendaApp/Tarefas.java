package com.solucaoSistemas.AgendaApp;

import java.util.ArrayList;
import java.util.List;

import Utilitarios.MyString;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class Tarefas extends Activity{
	private ConectaLocal conectTarefa;
	public List<String> TAREFAS = new ArrayList<String>();
	public  ListView lst_tarefas;
	public RadioGroup radioGroupSituacao;
//	private List<String> selecionados = new ArrayList<String>();
	private static  String LOG = "teste";
	public TarefasAdapter lsvTarefasAdapter;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_tarefas);
   
			getActionBar().setDisplayShowHomeEnabled(false);
//			getActionBar().hide();
	        
	        conectTarefa = new ConectaLocal(getApplicationContext(), "TAREFA");	        
			conectTarefa.setOrder(" ORDER BY CDTAREFA");			
			conectTarefa.setClausula("");			
			String[] aux = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
	         for(String array : aux){
	        	 TAREFAS.add(MyString.tiraEspaço(array));
	         }
				         
	        this.radioGroupSituacao = (RadioGroup)findViewById(R.id.radioGroupSituacao);
			this.lst_tarefas = (ListView)findViewById(R.id.lst_tarefas);
			this.lst_tarefas.setItemsCanFocus(true);	
			
			
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
	        
	        
	        radioGroupSituacao.setOnCheckedChangeListener(new OnCheckedChangeListener()
	        {
	            @Override
	            public void onCheckedChanged(RadioGroup group, int checkedId)
	            {
	            	lsvTarefasAdapter.notifyDataSetInvalidated();
	            	TAREFAS.clear();
	    			conectTarefa.setOrder(" ORDER BY CDTAREFA");			
	                switch(checkedId)
	                {
	                case R.id.radio0:
		    			conectTarefa.setClausula("");			
		    			String[] aux = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
		    	         for(String array : aux){
		    	        	 TAREFAS.add(MyString.tiraEspaço(array));
		    	         }
	                    break;
	                case R.id.radio1:
		    			conectTarefa.setClausula(" WHERE CDSTATUS='B'");			
		    			String[] aux1 = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
		    	         for(String array : aux1){
		    	        	 TAREFAS.add(MyString.tiraEspaço(array));
		    	         }
	                    break;
	                case R.id.radio2:
		    			conectTarefa.setClausula(" WHERE CDSTATUS='A'");			
		    			String[] aux2 = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
		    	         for(String array : aux2){
		    	        	 TAREFAS.add(MyString.tiraEspaço(array));
		    	         }
	                    break;
	                }
	    			lsvTarefasAdapter = new TarefasAdapter(Tarefas.this, R.layout.listview_tarefas, TAREFAS);
	    	        lst_tarefas.setAdapter(lsvTarefasAdapter); 
	            }
	        });
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
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		conectTarefa.close();
		lsvTarefasAdapter.clear();
	}
}





