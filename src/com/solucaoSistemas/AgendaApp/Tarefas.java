package com.solucaoSistemas.AgendaApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Tarefas extends Activity{

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_tarefas);
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.tarefas, menu);
	     return true;
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();

	        switch (id){
	    		case R.id.action_novaTarefa:
		    		Intent intent = new Intent(Tarefas.this, InsereTarefa.class);
		    		Tarefas.this.startActivity(intent);
		    		Tarefas.this.finish();
		    		return true;		    		
	    		case R.id.action_eventos:
		    		Intent intent2 = new Intent(Tarefas.this, Principal.class);
		    		Tarefas.this.startActivity(intent2);
		    		Tarefas.this.finish();
		    		return true;	
	        }
		            return super.onOptionsItemSelected(item);
	 }
	 
	 
}
