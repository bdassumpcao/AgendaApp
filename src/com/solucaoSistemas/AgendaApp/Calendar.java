/**@author Bruno Lopes*/
package com.solucaoSistemas.AgendaApp;

//import java.text.SimpleDateFormat;
//import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

import com.solucaoSistemas.AgendaApp.R;


@SuppressLint("SimpleDateFormat")
public class Calendar extends Activity{
	CalendarView calendario;
	Button bAvanca, opcoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Principal();
   }
    
   @SuppressLint("SimpleDateFormat") 
   public void Principal(){
	   setContentView(R.layout.activity_calendario);
       
	   final CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView1);  	    
    
//	   showToast( format.format( new Date() ) );           
	   // quando selecionado alguma data diferente da padrão
	   calendarView.setOnDateChangeListener(selecionaData());
   }     


	public OnDateChangeListener selecionaData(){
	return new OnDateChangeListener() {
   	   
       @Override
       public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
    	    String dia = null, mes = null,ano = null;
       		month+= 1;
       		if(dayOfMonth<10)
       			dia = "0"+dayOfMonth;
       		else dia = ""+dayOfMonth;
       		if(month<10)
       			mes = "0"+month;
       		else mes = ""+month;
       		ano = ""+(year);
       		String data = dia+"/"+mes+"/"+ano;     		
	        Intent intent = new Intent(Calendar.this, Principal.class);
	        Bundle b = new Bundle();
	    	b.putString("key", data);
	    	intent.putExtras(b);
	        startActivity(intent);
	        Calendar.this.finish();		        
       }                       
   };
}
	
	@Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	   	MenuInflater inflater = getMenuInflater();
	   	inflater.inflate(R.menu.calendario, menu);
	       return true;
	   }

	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       // Handle item selection
		   Intent intent = new Intent(Calendar.this, Principal.class);
	       switch (item.getItemId()) {
	           case R.id.action_hoje:
	               mostraEventos(intent);
	               return true;
	           default:
	               return super.onOptionsItemSelected(item);
	       }
	   }
	   
	   public void mostraEventos(Intent intent){ 
		    SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" ); 	        
	        Bundle b = new Bundle();
	    	b.putString("key", format.format(new Date()));
	    	intent.putExtras(b);
	        startActivity(intent);
	        Calendar.this.finish();	  	
	   }
	
	public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
	}
}
