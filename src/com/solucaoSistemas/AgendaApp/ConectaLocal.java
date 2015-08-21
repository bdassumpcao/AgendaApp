package com.solucaoSistemas.AgendaApp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author maxissuel
 *
 */
public class ConectaLocal {
	private DatabaseHelper data;
	private SQLiteDatabase db;
	private String tabela = "";
	private String clausula = "";
	private String order = "";

	/**
	 * @param data
	 */
	public ConectaLocal(Context con, String tabela) {
		data = new DatabaseHelper(con);
		db = data.getDatabase();
		setTabela(tabela);
	}

	public void executa(String sql){
		 db.execSQL(sql);
	}
	
	public Object select(String campos){
		List<Object> result = new ArrayList<Object>();
		
		Cursor cursor = db.rawQuery("SELECT "+ campos +" FROM "+ tabela +" "+ clausula + order, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			JSONObject obj = new JSONObject();
			
			try{
				obj.put("", cursor.getString(0)+'$');

			}catch(JSONException e){
				
			}
			
			result.add(obj);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return result;
	}
	
	public Object insert(String valores){
		List<Object> result = new ArrayList<Object>();
		String operation = "INSERT INTO "+tabela+" VALUES ("+valores+")";
		
		Cursor cursor = db.rawQuery(operation, null);
		
		cursor.moveToFirst();
		while(cursor!=null && !cursor.isAfterLast()){
			JSONObject obj = new JSONObject();
			
			try{
				obj.put("", cursor.getString(0));
			}catch(JSONException e){
				
			}
			
			result.add(obj);
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return operation;
	}
	
	public ArrayList<String> selectRelatorio(String sql, int numColuna){
		ArrayList<String> result = new ArrayList<String>();
		
		Cursor cursor = db.rawQuery(sql, null);
		
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
		
				for(int i = 0; i < numColuna; i++){
					result.add(cursor.getString(i));
				}
			
			result.add("\n-----------------\n");
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return result;
	}
	
	public void update(String campos){
		 db.execSQL("UPDATE "+tabela+" SET "+ campos + clausula);
	}
	
	
	public boolean delete(){
		try {
			db.execSQL("DELETE FROM "+tabela+clausula);
		} catch (Exception e) {
			return false;
		}
		return true;
		
	}
	
	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}
	
	public void setClausula(String clausula) {
		this.clausula = clausula;
	}
	
	public String getclausula(){
		return clausula;
	}
	
	public void pegaUltimo(String c){
		this.clausula = c;
	}
	
	public void setOrder(String order){
		this.order = order;
	}
}
