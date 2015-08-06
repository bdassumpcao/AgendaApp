package com.solucaoSistemas.AgendaApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Utilitarios.MyString;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TarefasAdapter extends ArrayAdapter<String>{
	private static  String LOG = "teste";
	private Context mContext;
	private int mInflater;
//	public String[] TAREFAS;
	private List<String> TAREFAS = new ArrayList<String>();
	private List<String> selecionados = new ArrayList<String>();
	private ConectaLocal conectUser;
	private ConectaLocal conectTarefa;
	private ConectaLocal conectLogTarefa;
	private AlertDialog alerta;
	private int tamanho;
	
	
    public TarefasAdapter(Context context, int layoutResourceId ,List<String> lista) {
    	super(context, layoutResourceId, lista);
         this.mContext = context;
         this.mInflater = layoutResourceId;
         this.TAREFAS = lista;
         this.tamanho = lista.size();
         
         Log.i(LOG, "TarefasAdapter()");
         
	     conectUser = new ConectaLocal(mContext, "USUARIO"); 
	     conectTarefa = new ConectaLocal(mContext, "TAREFA");
	     conectLogTarefa= new ConectaLocal(mContext, "LOGTAREFA");
	     selecionados.clear();
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return TAREFAS.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return TAREFAS.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(position<tamanho){	        
	        // Se o ConvertView for diferente de null o layout já foi "inflado"   
	        if(v==null) {
	            // "Inflando" o layout do item caso o isso ainda não tenha sido feito
	        	LayoutInflater inflater = (LayoutInflater) mContext
	        	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = (View) inflater.inflate(mInflater, null);
	        }

	        final String cdTarefa = TAREFAS.get(position);
	        conectTarefa.setClausula(" WHERE CDTAREFA="+cdTarefa);
	        final String resp = MyString.tiraEspaço(MyString.tString(conectTarefa.select(" CDRESPONSAVEL ")));
	        //SELECT CDDESTINATARIO FROM TAREFA WHERE (SELECT CDREFERENCIA FROM TAREFA WHERE CDTAREFA=1)
	
	        
	        conectTarefa.setClausula(" WHERE CDRESPONSAVEL="+resp+" "
	        		+ "AND CDREFERENCIA=(SELECT CDREFERENCIA FROM TAREFA WHERE CDTAREFA="+cdTarefa+")");
	        String[] dest = MyString.tStringArray(conectTarefa.select(" CDDESTINATARIO "));
	        String[] cd = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
	        
	        String destinatarios = "";
	        for(int i=0; i<dest.length; i++){
	        	if(i==(dest.length-1))
	        		destinatarios += getNmUsuario(MyString.tiraEspaço(dest[i]));
	        	else
	        		destinatarios += getNmUsuario(MyString.tiraEspaço(dest[i]))+",";
	        }
	        
	        for(int j=0; j<cd.length; j++){     
	        	if(!MyString.tiraEspaço(cd[j]).equals(cdTarefa)){
	        		if(TAREFAS.contains(MyString.tiraEspaço(cd[j]))){
	        			TAREFAS.remove(MyString.tiraEspaço(cd[j])); 
	        			tamanho--;
	        			notifyDataSetChanged();
	        		}
	        		
	        	}
	        	
	        }

	        // Recuperando o checkbox
	        final CheckBox check = (CheckBox) v.findViewById(R.id.check_tarefa);
	
	        conectTarefa.setClausula(" WHERE CDTAREFA="+cdTarefa);
	        String descricao = MyString.tString(conectTarefa.select(" NMDESCRICAO "));
	        String responsavel = getNmUsuario(MyString.tString(conectTarefa.select(" CDRESPONSAVEL ")));
	//        String destinatarios = getNmDestinatarios(MyString.tString3(conectTarefa.select(" CDDESTINATARIO ")));
	        String status = MyString.tString(conectTarefa.select(" CDSTATUS "));
	        String dataLanc = (MyString.tString(conectTarefa.select(" DTLANCAMENTO "))).replace("\\", "");
	        String dataConc = (MyString.tString(conectTarefa.select(" DTBAIXA "))).replace("\\", "");
	
	        if(status.equals("1")) {
	            check.setChecked(true);
	            selecionados.add(cdTarefa);
	        } else {
	            check.setChecked(false);
	            selecionados.remove(cdTarefa);
	        }
	        
	
	        TextView txtv_Descricao = (TextView) v.findViewById(R.id.txtv_Descricao);
	        TextView txtv_nmResponsavel = (TextView) v.findViewById(R.id.txtv_nmResponsavel);
	        TextView txtv_nmDestinatario = (TextView) v.findViewById(R.id.txtv_nmDestinatarios);
	        TextView txtv_nmDataLanc = (TextView) v.findViewById(R.id.txtv_nmDataLanc);
	        TextView txtv_nmDataConc = (TextView) v.findViewById(R.id.txtv_nmDataConc);
	        txtv_Descricao.setText(descricao.trim());
	        txtv_nmResponsavel.setText(responsavel.trim());
	        txtv_nmDestinatario.setText(destinatarios.trim());
	        if(dataLanc.equals("null"))
	        	dataLanc = "";
	        txtv_nmDataLanc.setText(dataLanc.trim());
	        if(dataConc.equals("null"))
	        	dataConc = "";
	        txtv_nmDataConc.setText(dataConc.trim());
	        
	            
	        Button btn_deletar = (Button) v.findViewById(R.id.btn_deletar);
	        btn_deletar.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext);
	    			builder2.setMessage("Confirma exclusão ?");
	    	        builder2.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	    	        	public void onClick(DialogInterface arg0, int arg1) {
	    	       			deletar(cdTarefa, resp, position);	       			
	    	        	}
	    	        });
	    	        builder2.setNegativeButton("Não", new DialogInterface.OnClickListener() {
	    	        	public void onClick(DialogInterface arg0, int arg1) {
	    	        		
	    	        	}
	    	        });
	    	        alerta = builder2.create();
	    	        alerta.show();
	
	            }
	        });
	        
	        
	        check.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
		            CheckBox cb = (CheckBox) v.findViewById(R.id.check_tarefa);	
		            
		    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    		Date data = new Date();
		    		Calendar c = Calendar.getInstance();
		    		c.setTime(data);
		    		Date dataAtual = c.getTime();
		    		data.getTime();
		    		
		    		final String actualData = dateFormat.format(dataAtual);
		    		
		            if (cb.isChecked()) {	 	  
		            	Log.i(LOG, "cb.isChecked"+cdTarefa);
		                selecionados.add(cdTarefa);
		                conectTarefa.setClausula(" WHERE CDTAREFA="+cdTarefa);
		                conectTarefa.update(" CDSTATUS=1 ");
		                conectTarefa.update(" DTBAIXA='"+actualData+"'");
		                Log.i(LOG, actualData);
		            } else if (!cb.isChecked()) {
		            	Log.i(LOG, "!cb.isChecked"+cdTarefa);
		                selecionados.remove(cdTarefa);
		                conectTarefa.setClausula(" WHERE CDTAREFA="+cdTarefa);
		                conectTarefa.update(" CDSTATUS=0 ");
		                conectTarefa.update("  DTBAIXA="+"null");		               
		            }
		            selecionados(check, cdTarefa);
		            notifyDataSetChanged();
	            }
	        });

	        selecionados(check, cdTarefa);
	        
        
        }
		return v;
        
    }
	
	public void selecionados(CheckBox check, String cdTarefa){
        if(selecionados.contains(cdTarefa)) {
        	Log.i(LOG, "selecionados"+cdTarefa);
            check.setChecked(true);
        } else {
        	Log.i(LOG, "!selecionados"+cdTarefa);
            check.setChecked(false);
        }
	}
	
	public void deletar(String cd, String resp, int position){
		conectTarefa.setClausula(" WHERE CDRESPONSAVEL="+resp+" AND "
				+ "CDREFERENCIA=(SELECT CDREFERENCIA FROM TAREFA WHERE CDTAREFA="+cd+")");
		Log.i(LOG, "codigo que vai excluir:"+cd);
		String[] cdT = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
		
		for(int i=0; i<cdT.length; i++){
			conectTarefa.setClausula(" WHERE CDTAREFA='"+cdT[0]+"'");
			String ref = MyString.tString(conectTarefa.select(" CDREFERENCIA "));
			String dest = MyString.tString(conectTarefa.select(" CDDESTINATARIO "));
			conectLogTarefa.insert(cdT[0]+","+ref+","+dest+","+resp+","+"'D'");		
		}		
		
		TAREFAS.remove(cd);
		conectTarefa.setClausula(" WHERE CDRESPONSAVEL="+resp+" AND "
				+ "CDREFERENCIA=(SELECT CDREFERENCIA FROM TAREFA WHERE CDTAREFA="+cd+")");
		conectTarefa.delete();			
		
		notifyDataSetChanged();
	}
	
	 public String getUsuarioAtivo(){
		 conectUser.setClausula(" WHERE STATUS=1");
		 String cd = MyString.tString(conectUser.select(" CDUSUARIO "));
		 return cd;
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

}
