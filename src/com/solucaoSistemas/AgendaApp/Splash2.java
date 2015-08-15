package com.solucaoSistemas.AgendaApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import Utilitarios.MyString;
import Web.Conexao;
import Web.ExecutaWeb;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;

public class Splash2 extends Activity {
	private static  String LOG = "teste";
	public boolean pendencia = false;
	ConectaLocal conectTarefa;
	ConectaLocal conectUser;
	ConectaLocal conectLogTarefa;
	ArrayList<Thread> listaThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setFinishOnTouchOutside(false);
		
		Log.i(LOG, "SPLASH2 TAREFA");
		
		conectTarefa = new ConectaLocal(this, "TAREFA");
		conectUser = new ConectaLocal(this, "USUARIO");
		conectLogTarefa = new ConectaLocal(this, "LOGTAREFA");
		
		listaThread = new ArrayList<Thread>();
		
		setContentView(R.layout.activity_splash2);
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					monitor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				Splash2.this.finish();
			}
		});
		t.start();
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 * */
	public void  monitor() throws InterruptedException, UnsupportedEncodingException{
		Log.i(LOG, "entrou monitor() TAREFA");
		Conexao conexao = new Conexao(this);
		final String url;
		
		if(conexao.isConected()){
			url = conexao.pegaLink();
			Log.i(LOG, "link:\n"+url);
			
			//----------------------------------			
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i(LOG,"entrou updateServidor() TAREFA");
					try {
						updateServidor(url);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu updateServidor() TAREFA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			
			//----------------------------------			
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i(LOG,"entrou deleteServidor() TAREFA");
					try {
						deleteServidor(url);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu deleteServidor() TAREFA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			//---------------------------------------------
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i(LOG,"entrou selectCelular() TAREFA");
					try {
						selectCelular(url);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu selectCelular() TAREFA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			//----------------------------------------------
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					conectTarefa.setClausula("");
					conectTarefa.delete();
					Log.i(LOG,"Apagou dados da TAREFA");
					Log.i(LOG,"entrou selectServidor() TAREFA");
					try {						
						selectServidor(url);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu selectServidor() TAREFA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			//----------------------------------------------
			
			for(Thread t: listaThread){
				if(!t.isAlive()){
					t.start();
					}
				Log.i(LOG, t.getName()+"");
				while (t.isAlive()) {
					Thread.sleep(1000);
				}
				
			}
			listaThread.clear();
		}
		else{
			Log.i(LOG, "Não Conectado");
		}
		Log.i(LOG, "saiu monitor() TAREFA");
	}
	
	
	public void updateServidor(String url) throws InterruptedException{	
		String[] cdT, cdResp, cdRef;
		String cdStatus, dtBaixa;
		String dados = "";
		String respServer = "";

		conectLogTarefa.setOrder("");
		conectLogTarefa.setClausula(" WHERE DSOPERACAO='U' ");
		cdT = MyString.tStringArray(conectLogTarefa.select(" CDTAREFA "));
		cdResp = MyString.tStringArray(conectLogTarefa.select(" CDRESPONSAVEL "));
		cdRef = MyString.tStringArray(conectLogTarefa.select(" CDREFERENCIA "));
				
		for(int j=0; j<cdT.length; j++){
			Log.i(LOG, "cdT[j]"+cdT[j]);
			cdResp[j] = MyString.tiraEspaço(cdResp[j]);
			cdRef[j] = MyString.tiraEspaço(cdRef[j]);
			
			conectTarefa.setClausula(" WHERE CDTAREFA="+cdT[j]);
			cdStatus = MyString.tString(conectTarefa.select(" CDSTATUS "));
			dtBaixa = (MyString.tString4(conectTarefa.select("DTBAIXA"))).replace('/' , '.');
			if(dtBaixa.equals("null"))
				dtBaixa = "";
			
			if(userAtivo().equals(cdResp[j])){
				dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=ur&cdResp="+cdResp[j]+
						"&cdRef="+cdRef[j]+"&cdStatus="+cdStatus+"&dtBaixa="+dtBaixa;			
			}
			
			if(!userAtivo().equals(cdResp[j])){
				dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=ud&cdResp="+cdResp[j]+
						"&cdRef="+cdRef[j]+"&cdDest="+userAtivo()+"&cdStatus="+cdStatus+"&dtBaixa="+dtBaixa;
			}
			
			respServer = webservice(url, dados);
			respServer = respServer.substring(0, respServer.indexOf("#"));
			Log.i(LOG, "respServer == "+respServer);
			if(respServer.equals("")){
				conectLogTarefa.setClausula(" WHERE DSOPERACAO='U' AND CDTAREFA="+cdT[j]);
				conectLogTarefa.delete();
				Log.i(LOG, cdT[j]+"atualizado do servidor");	
			}					
		}				
	}
	
	public void deleteServidor(String url) throws InterruptedException {
		String[] cdT, cdResp, cdRef, cdDest;
		String dados = "";
		String respServer = "";

		conectLogTarefa.setOrder("");
		conectLogTarefa.setClausula(" WHERE DSOPERACAO='D' ");
		cdT = MyString.tStringArray(conectLogTarefa.select(" CDTAREFA "));
		cdResp = MyString.tStringArray(conectLogTarefa.select(" CDRESPONSAVEL "));
		cdRef = MyString.tStringArray(conectLogTarefa.select(" CDREFERENCIA "));
		cdDest = MyString.tStringArray(conectLogTarefa.select(" CDDESTINATARIO "));
				
		for(int j=0; j<cdT.length; j++){
			Log.i(LOG, "cdT[j]"+cdT[j]);
			cdResp[j] = MyString.tiraEspaço(cdResp[j]);
			cdRef[j] = MyString.tiraEspaço(cdRef[j]);
			cdDest[j] = MyString.tiraEspaço(cdDest[j]);
			
			if(userAtivo().equals(cdResp[j])){
				dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=dr&cdResp="+cdResp[j]+"&cdRef="+cdRef[j];			
			}
			
			if(!userAtivo().equals(cdResp[j])){
				Log.i(LOG, "!userAtivo().equals(cdResp[j])");
				dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=dd&cdResp="
				+cdResp[j]+"&cdRef="+cdRef[j]+"&cdDest="+cdDest[j];
			}
			
			respServer = webservice(url, dados);
			respServer = respServer.substring(0, respServer.indexOf("#"));
			Log.i(LOG, "respServer == "+respServer);
			if(respServer.equals("")){
				conectLogTarefa.setClausula(" WHERE DSOPERACAO='D' AND CDTAREFA="+cdT[j]);
				conectLogTarefa.delete();
				Log.i(LOG, cdT[j]+"deletado do servidor");	
			}					
		}		
	}
	

	/**
	 * 
	 * @param url
	 * @throws InterruptedException
	 */
	public void selectServidor(String url) throws InterruptedException{	
		String cdU = userAtivo();
		String dados = "";
		String respServer = "";
		
		dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=sar&cdU="+cdU;			
		respServer = webservice(url, dados);
		respServer = respServer.substring(0, respServer.indexOf("#"));
		Log.i(LOG, "respServer == "+respServer);
		if(!respServer.equals(""))
			insereCelular(respServer);
		
		dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=sad&cdU="+cdU;	
		respServer = webservice(url, dados);
		respServer = respServer.substring(0, respServer.indexOf("#"));
		Log.i(LOG, "respServer == "+respServer);
		if(!respServer.equals(""))
			insereCelular(respServer);
	}
	
	public void insereCelular(String respServer){
		Log.i(LOG, "insereCelular() TAREFA");
		if(!MyString.normalize(respServer).equals("")){
			try {
				String[] campos = MyString.montaInsertTarefa(respServer);
				
				for(String i : campos){
					conectTarefa.insert(i);
					Log.i(LOG, i+" |inserido no celular");
				}
//				geraNotificacaoNovaTarefa();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Seleciona as tarefas do celular que serão inseridas no servidor
	 * @param url
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	public void selectCelular(String url) throws InterruptedException, UnsupportedEncodingException{	
		String cdU = userAtivo();
		String respServer;
		
		String dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=sc&cdU="+cdU;
		respServer = webservice(url, dados);
		
		respServer = respServer.substring(0, respServer.indexOf("$"));
		respServer = MyString.normalize(respServer);
		Log.i(LOG, "respServer:"+respServer+"");
		
		
		//Se webservice retornar "" então selecionamos todas as tarefas do celular para inserir
		if(respServer.equals("")){			
			String ultRef = pegaUltimo(" CDREFERENCIA ", cdU);
			Log.i(LOG, "ultimoCdCelular"+ultRef);			
			
			//Se ultimoCdCelular for igual a -1 não executa o restante pois não tem tarefas para inserir
			if(!ultRef.equals("-1")){
				
				String  cdTarefa, descricao, dest, responsavel, status, cdRef, dtLanc, dtBaixa;
				conectTarefa.setOrder("");
				conectTarefa.setClausula(" WHERE CDRESPONSAVEL='"+userAtivo()+"'");
				String[] cdE = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectTarefa.setClausula(" WHERE CDTAREFA="+cdE[i]);
					
					cdTarefa = cdE[i];
					descricao = MyString.tString(conectTarefa.select("NMDESCRICAO"));
					descricao = URLEncoder.encode(descricao, "UTF-8");
					dest = MyString.tString(conectTarefa.select("CDDESTINATARIO"));
					dest = URLEncoder.encode(dest, "UTF-8");
					responsavel = MyString.tString(conectTarefa.select("CDRESPONSAVEL"));
					responsavel = URLEncoder.encode(responsavel, "UTF-8");
					status = MyString.tString(conectTarefa.select("CDSTATUS"));	
					cdRef = MyString.tString(conectTarefa.select("CDREFERENCIA"));					
					dtLanc = (MyString.tString4(conectTarefa.select("DTLANCAMENTO"))).replace('/' , '.');
					dtBaixa = (MyString.tString4(conectTarefa.select("DTBAIXA"))).replace('/' , '.');
					if(dtBaixa.equals("null"))
						dtBaixa = "";					

					String campos = "descricao="+descricao+"&destinatario="+dest+"&responsavel="+responsavel+"&status="+status+"&cdRef="+cdRef+"&dtLanc="+dtLanc+"&dtBaixa="+dtBaixa;
					Log.i(LOG, "campos enviados="+campos);
					Log.i(LOG, "campos enviados="+campos);
					insereServidor(url, campos);							
					Log.i(LOG, cdTarefa+" inserido no servidor");														
				}
			}
		}
		//Seleciona apenas tarefas que ainda não foram adicionadas no servidor
		else if(!respServer.equals("")){	
			
			int ultRefServ = 0;
			try {
				ultRefServ = Integer.parseInt(respServer);
			} catch(NumberFormatException e) {
			   Log.i(LOG, "Could not parse " + e);
			} 
			Log.i(LOG, "ultrefServ:"+ultRefServ+"");
			int ultRefCel = Integer.parseInt(pegaUltimo(" CDREFERENCIA ", cdU));
			Log.i(LOG, "ultrefCel:"+ultRefCel+"");
			if(ultRefCel != -1)
			if(ultRefCel>ultRefServ){
				String  cdTarefa, descricao, dest, responsavel, status, cdRef, dtLanc, dtBaixa;;
				conectTarefa.setOrder("");
				conectTarefa.setClausula(" WHERE CDREFERENCIA>"+ultRefServ+" AND CDRESPONSAVEL='"+userAtivo()+"'");
				String[] cdE = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectTarefa.setClausula(" WHERE CDTAREFA="+cdE[i]);
										
					cdTarefa = cdE[i];
					descricao = MyString.tString(conectTarefa.select("NMDESCRICAO"));
					descricao = URLEncoder.encode(descricao, "UTF-8");
					dest = MyString.tString(conectTarefa.select("CDDESTINATARIO"));
					dest = URLEncoder.encode(dest, "UTF-8");
					responsavel = MyString.tString(conectTarefa.select("CDRESPONSAVEL"));
					responsavel = URLEncoder.encode(responsavel, "UTF-8");
					status = MyString.tString(conectTarefa.select("CDSTATUS"));	
					cdRef = MyString.tString(conectTarefa.select("CDREFERENCIA"));	
					dtLanc = (MyString.tString4(conectTarefa.select("DTLANCAMENTO"))).replace('/' , '.');
					dtBaixa = (MyString.tString4(conectTarefa.select("DTBAIXA"))).replace('/' , '.');
					if(dtBaixa.equals("null"))
						dtBaixa = "";

					String campos = "descricao="+descricao+"&destinatario="+dest+"&responsavel="+responsavel+"&status="+status+"&cdRef="+cdRef+"&dtLanc="+dtLanc+"&dtBaixa="+dtBaixa;
					Log.i(LOG, "");
					Log.i(LOG, "campos a ser inseridos="+campos);
					insereServidor(url, campos);	
					Log.i(LOG, cdTarefa+" inserido no servidor");
				}
			}			
		}		
	}
	

	/**
	 * Insere tarefa no servidor e retorna o codigo gerado
	 * @param url
	 * @param campos
	 * @return cdExt
	 * @throws InterruptedException
	 */
	public String insereServidor(String url, String campos) throws InterruptedException{	
		Log.i(LOG, "entrou insereServidor()");
		String cdRef= "";
		String dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=i&"+campos;
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://"+url+dados);
		Log.i(LOG,"http://"+url+dados);
		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
		
		exec.start();
		
		do{
//			Log.i(LOG,"sleep");
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
		
		String respServer = exec.respServer.substring(0, exec.respServer.indexOf("$"));
		respServer = MyString.normalize(respServer);
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == vazio"+respServer);
		}
		else{
			cdRef = respServer;
		}
		return cdRef;
		
	}
	
	/**
	 * @param url
	 * @param dados
	 * @return exec.respServer
	 * @throws InterruptedException
	 */
	public String webservice(String url, String dados) throws InterruptedException{
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://"+url+dados);
		Log.i(LOG,"http://"+url+dados);
		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
		
		exec.start();
		
		do{
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
		return exec.respServer;
	}
	
	public void geraNotificacaoNovaTarefa(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Tarefas.class), "Novas Tarefas Adicionadas", "Tarefas", "Voce tem novas tarefas.");
	}
	
	public void geraNotificacaoTarefaBaixada(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Tarefas.class), "Novos eventos baixados", "Eventos", "Eventos foram baixados em sua agenda");
	}

	public void gerarNotificacao(Context context, Intent intent, CharSequence ticker, CharSequence titulo, CharSequence descricao){
		NotificationManager nm1 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent p1 = PendingIntent.getActivity(context, 0, intent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker(ticker);
		builder.setContentTitle(titulo);
		builder.setContentText(descricao);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
		builder.setContentIntent(p1);
		
		Notification n1 = builder.build();
		n1.vibrate = new long[]{150, 300, 150, 600};
		n1.flags = Notification.FLAG_AUTO_CANCEL;
		nm1.notify(R.drawable.ic_launcher, n1);
		
		try{
			Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone toque = RingtoneManager.getRingtone(context, som);
			toque.play();
		}
		catch(Exception e){}
	}
	
	public List<String> getNmDestinatarios(String string){
		List<String> lista = new ArrayList<String>();
		String nm = "";
		char[] aux = new char[string.length()];
	
		for(int i = 0; i < string.length(); i++){
			aux[i] = string.charAt(i);
			if(aux[i] == ','){
			}
		}
		
		for(int i = 0; i < aux.length; i++){
			if(aux[i] == ','){
				lista.add(nm);
				nm = "";
			}
			else if(i == aux.length-1){
				nm += aux[i];
				lista.add(nm);
				nm = "";
			}
			else{
				nm += aux[i];
			}
		}
		
		return lista;
	}
	
	public String pegaUltimo(String campo, String cdU){
		conectTarefa.setClausula(" WHERE CDRESPONSAVEL='"+cdU+"'");	
		String[] aux = MyString.tStringArray(conectTarefa.select(campo));
		
		if(aux.length>0)
			return MyString.tiraEspaço(aux[aux.length-1]);
		else
			return "-1";
	}
	
	public String userAtivo(){
		Log.i(LOG, "userAtivo()");
		conectUser.setClausula(" WHERE STATUS=1 ");
		conectUser.setOrder(" ORDER BY CDUSUARIO ");
		return MyString.tString(conectUser.select(" CDUSUARIO "));
	}
	
	public void updateCodServidor(String cdRef, String cdE) throws InterruptedException{
		Log.i(LOG, "updateCodServidor()");
		Conexao conexao = new Conexao(this);
		String url = conexao.pegaLink();
		String dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=uc&cdE="+cdRef+"&cdRef="+cdE;
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://"+url+dados);
		Log.i(LOG,"http://"+url+dados);
		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
		
		exec.start();
		
		do{
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
	}
	
	public boolean getPendencia(){
		return this.pendencia;
	}
	
	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
}
