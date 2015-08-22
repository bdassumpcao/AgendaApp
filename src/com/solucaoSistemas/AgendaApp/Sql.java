/**@author Bruno Lopes*/
package com.solucaoSistemas.AgendaApp;

import Utilitarios.MyString;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Sql extends Activity {
	  ConectaLocal conectBanco;
	  EditText edt_sql;
	  RadioButton selectAgenda;
	  RadioButton selectTarefa;
	  RadioButton updateDelete;
	  TextView result;
	  
	  public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.activity_sql);
		    
		    conectBanco = new ConectaLocal(this, "");
		    edt_sql = (EditText)findViewById(R.id.edt_sql);
		    updateDelete = (RadioButton)findViewById(R.id.updateDelete);
		    selectAgenda = (RadioButton)findViewById(R.id.selectAgenda);
		    selectTarefa = (RadioButton)findViewById(R.id.selectTarefa);
		    result = (TextView) findViewById(R.id.result);
	  }
	  
	  public void executarSql(View view){
		  String sql = edt_sql.getText().toString();
		  if(!sql.equals("")){
			  if(selectAgenda.isChecked()){
				  String aux = MyString.tString(conectBanco.selectRelatorio(sql, 10));
				  result.setText(aux);
			  }
			  else if(selectTarefa.isChecked()){
				  String aux = MyString.tString(conectBanco.selectRelatorio(sql, 8));
				  result.setText(aux);
			  }
			  else{
				  conectBanco.executa(edt_sql.getText().toString());
				  result.setText(sql);
				  showToast(sql);
			  }
		  }
	  }
	  
	 public void showToast(String texto){		
		 Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
	 }
	 
	@Override
	public void onDestroy(){
		super.onDestroy();
		conectBanco.close();
	}
}
