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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ServiceTarefas extends Service{
	private static  String LOG = "teste";
	private static String[] cod;
	public boolean pendencia = false;
	ConectaLocal conectTarefa;
	ConectaLocal conectUser;
	
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.i(LOG, "onCreate() TAREFA");
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i(LOG, "onStartCommand() TAREFA");
		
		if(!pendencia){
			pendencia = true;

				try{
					conectTarefa = new ConectaLocal(this, "TAREFA");
					conectUser = new ConectaLocal(this, "USUARIO");
//					conectLogAgenda = new ConectaLocal(getApplicationContext(), "LOGAGENDA");
					monitor();
				}catch(Exception e){
					Log.i(LOG, "erro no monitor\n"+e);
				}
		}
		
		onDestroy();
		
		return(START_STICKY);
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 * */
	public void  monitor() throws InterruptedException, UnsupportedEncodingException{
		Log.i(LOG, "entrou monitor() TAREFA");
		Conexao conexao = new Conexao(this);
		String url = "";
		
		if(conexao.isConected()){
			url = conexao.pegaLink();
			Log.i(LOG, "link:\n"+url);
			
			//----------------------------------
			
			Log.i(LOG,"entrou selectCelular()");
			selectCelular(url);
			Log.i(LOG,"saiu selectCelular()");
			Log.i(LOG, "");
			
			Log.i(LOG,"entrou selectServidor()");
			selectServidor(url);
			Log.i(LOG,"saiu selectServidor()");
			Log.i(LOG, "");


		}
		else{
			Log.i(LOG, "N�o Conectado");
		}
		Log.i(LOG, "saiu monitor() TAREFA");
	}
	
	
//	public void updateServidor(String url) throws InterruptedException, UnsupportedEncodingException{	
//		String cdU = userAtivo();
//		String[] cdE;				
//
//		conectLogAgenda.setOrder("");
//		conectLogAgenda.setClausula(" WHERE OPERACAO='U' ");
//		cdE = MyString.tStringArray(conectLogAgenda.select(" CDEVENTOEXT "));
////		String  cdEvento;
//		String desc, lc, obs, dt, hI, hF, st;
//		
//		if(cdE.length!=0){
//		for(int j=0; j<cdE.length; j++){
//			if(!cdE[j].equals("null")){
//			Log.i(LOG, "cdE[j]"+cdE[j]);		
//			conectAgenda.setOrder("");
//			conectAgenda.setClausula(" WHERE CDEVENTOEXT="+cdE[j]);	
//			
////			cdEvento = MainActivity.tString(conectAgenda.select("CDEVENTO"));
//			desc = MyString.tString(conectAgenda.select("DESCRICAO"));
//			desc = URLEncoder.encode(desc, "UTF-8");
//			lc = MyString.tString(conectAgenda.select("LOCAL"));
//			lc = URLEncoder.encode(lc, "UTF-8");
//			obs = MyString.tString(conectAgenda.select("OBSERVACAO"));
//			obs = URLEncoder.encode(obs, "UTF-8");
//			dt = MyString.tString(conectAgenda.select("DATA"));
//			dt = dt.replace( "\\" , ""); 
//			hI = MyString.tString(conectAgenda.select("HORAINICIO"));
//			String aux = hI.substring(0, 2) +":";
//			aux += hI.substring(2, 4);
//			hI = aux;
//			hF = MyString.tString(conectAgenda.select("HORAFIM"));
//			aux = hF.substring(0, 2) +":";
//			aux += hF.substring(2, 4);
//			hF = aux;
//			st = MyString.tString(conectAgenda.select("STATUS"));					
//			
//			String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=u&cdU="+cdU+"&cdE="+cdE[j]+"&desc="+desc+"&obs="+obs+"&st="+st+"&dt="+dt+"&hI="+hI+"&hF="+hF+"&lc="+lc;
//			ResponseHandler<String> handler = new BasicResponseHandler();
//			HttpClient client = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet("http://"+url+dados);	
//			Log.i(LOG,"http://"+url+dados);
//			ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);			
//			
//			exec.start();
//			
//			do{
////				Log.i(LOG,"sleep");
//				Thread.sleep(1000);
//			}
//			while(exec.respServer.equals(""));
//			
//			if(!exec.respServer.equals("$")){
//				Log.i(LOG, "respServer == "+exec.respServer);
//			}
//			else{
//				conectLogAgenda.setOrder("");
//				conectLogAgenda.setClausula(" WHERE CDEVENTOEXT="+cdE[j]);
//				conectLogAgenda.delete();
//				Log.i(LOG, cdE[j]+" alterado no servidor");
//			}
//			}
//		}
//		}
//		
//	}
	
//	public void deleteServidor(String url) throws InterruptedException{	
//		String cdU = userAtivo();
//		String[] cdE;				
//
//		conectLogAgenda.setOrder("");
//		conectLogAgenda.setClausula(" WHERE OPERACAO='D' ");
//		cdE = MyString.tStringArray(conectLogAgenda.select(" CDEVENTOEXT "));
//		String cdExt="";
//		
//		if(cdE.length!=0){
//		for(int j=0; j<cdE.length; j++){
//			Log.i(LOG, "cdE[j]"+cdE[j]);
//			if(!cdE[j].equals("null")){
//				if(j == cdE.length-1)
//					cdExt += MyString.tiraEspa�o(cdE[j]);
//				else
//					cdExt += MyString.tiraEspa�o(cdE[j])+","; 
//			}
//		}
//
//			String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=d&cdU="+cdU+"&cdE="+cdExt;
//			ResponseHandler<String> handler = new BasicResponseHandler();
//			HttpClient client = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet("http://"+url+dados);
//			Log.i(LOG,"http://"+url+dados);
//			ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
//			
//			exec.start();
//			
//			do{
////				Log.i(LOG,"sleep");
//				Thread.sleep(1000);
//			}
//			while(exec.respServer.equals(""));
//			
//			if(!exec.respServer.equals("$")){
//				Log.i(LOG, "respServer == "+exec.respServer);
//			}
//			else{
//				conectLogAgenda.setOrder("");
//				conectLogAgenda.setClausula(" WHERE OPERACAO='D' ");
//				conectLogAgenda.delete();
//				Log.i(LOG, cdExt+" excluido no servidor!");
//			}
//		}
//		
//	}
	
	
//	public void deleteCelular(String url) throws InterruptedException{	
//		String cdU = userAtivo();	
//
//		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sd&cdU="+cdU;
//		ResponseHandler<String> handler = new BasicResponseHandler();
//		HttpClient client = new DefaultHttpClient();
//		HttpGet httpGet = new HttpGet("http://"+url+dados);
//		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
//			
//		exec.start();
//			
//		do{
////			Log.i(LOG,"sleep");
//			Thread.sleep(1000);
//		}
//		while(exec.respServer.equals(""));
//		String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));
//
//		if(aux.equals("")){
//			Log.i(LOG, "respServer == "+aux);
//		}
//		else{
//			String[] campos = MyString.tStringArray(aux);
//			for(int i=0 ;i<campos.length ;i++){		
//					conectAgenda.setOrder("");
//					conectAgenda.setClausula(" WHERE CDEVENTOEXT="+campos[i]);
//					conectAgenda.delete();
//					Log.i(LOG, campos[i]+" excluido no celular");
//			}			
//		}
//	}	
//	
	
	/**
	 * 
	 * @param url
	 * @throws InterruptedException
	 */
	public void selectServidor(String url) throws InterruptedException{	
		String cdU = userAtivo();
		String cdRef = pegaUltimo(" CDREFERENCIA ", cdU);
		String dados = "";
		String respServer = "";
		
		if(cdRef.equals("-1")){
			dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=sar&cdU="+cdU;			
			respServer = webservice(url, dados);
			respServer = respServer.substring(0, respServer.indexOf("#"));
			Log.i(LOG, "respServer == "+respServer);
			insereCelular(respServer, true);
			
			
			dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=sad&cdU="+cdU;	
			respServer = webservice(url, dados);
			respServer = respServer.substring(0, respServer.indexOf("#"));
			Log.i(LOG, "respServer == "+respServer);
			insereCelular(respServer, false);
		}
		
		if(!cdRef.equals("-1")){
			dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=su&cdU="+cdU+"&cdE="+cdRef;
			respServer = webservice(url, dados);	
			respServer = respServer.substring(0, respServer.indexOf("#"));
			insereCelular(respServer, false);
			Log.i(LOG, "respServer == "+respServer);
		}

	}
	
	public void insereCelular(String respServer, boolean atualiza_referencia){
		Log.i(LOG, "insereCelular() TAREFA");
		if(!respServer.equals("")){
			try {
				String[] campos = MyString.montaInsertTarefa(respServer);
				cod = MyString.getCod();				
				int j = 0;
				
				for(String i : campos){
					conectTarefa.insert(i);
					Log.i(LOG, MyString.tString(conectTarefa.select(" CDTAREFA "))+" inserido no celular");
					if(atualiza_referencia){
						conectTarefa.setClausula(" WHERE CDREFERENCIA="+cod[j]);					
						updateCodServidor(MyString.tString(conectTarefa.select(" CDTAREFA ")), cod[j]);						
					}
					j++;
				}
				geraNotificacaoNovoEvento();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Seleciona as tarefas do celular que ser�o inseridas no servidor
	 * @param url
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("deprecation")
	public void selectCelular(String url) throws InterruptedException, UnsupportedEncodingException{	
		String cdU = userAtivo();
		List<String> destinatarios = new ArrayList<String>();
		String respServer;
		
		String dados = "/webservice/processo.php?flag=2&chave=l33cou&operacao=sc&cdU="+cdU;
		respServer = webservice(url, dados);
		
		String s = respServer.substring(0, respServer.indexOf("$"));
		Log.i(LOG, "respServer:'"+s+"'");
		
		//Se webservice retornar "" ent�o selecionamos todas as tarefas do celular para inserir
		if(s.equals("")){			
			String ultimoCdCelular = pegaUltimo(" CDREFERENCIA ", cdU);
			Log.i(LOG, "ultimoCdCelular"+ultimoCdCelular);			
			
			//Se ultimoCdCelular for igual a -1 n�o executa o restante pois n�o tem tarefas para inserir
			if(!ultimoCdCelular.equals("-1")){
				
				String  cdTarefa, descricao, dest, responsavel, status, cdRef;
				conectTarefa.setOrder("");
				conectTarefa.setClausula("");
				String[] cdE = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectTarefa.setClausula(" WHERE CDTAREFA="+cdE[i]);
					
					cdTarefa = MyString.tString(conectTarefa.select("CDTAREFA"));
					descricao = MyString.tString(conectTarefa.select("NMDESCRICAO"));
					descricao = URLEncoder.encode(descricao, "UTF-8");
					dest = MyString.tString3(conectTarefa.select("NMDESTINATARIOS"));
					responsavel = MyString.tString(conectTarefa.select("CDRESPONSAVEL"));
					responsavel = URLEncoder.encode(responsavel, "UTF-8");
					status = MyString.tString(conectTarefa.select("CDSTATUS"));	
					cdRef = MyString.tString(conectTarefa.select("CDREFERENCIA"));	
					
					
					destinatarios = getNmDestinatarios(dest);
					
					for(int x=0; x<destinatarios.size(); x++){
						String d = URLEncoder.encode(destinatarios.get(x), "UTF-8");
						String campos = "descricao="+descricao+"&destinatario="+d+"&responsavel="+responsavel+"&status="+status+"&cdRef="+cdRef;
						Log.i(LOG, campos);
						insereServidor(url, campos);	
						
//						String cdRef = insereServidor(url, campos);						
//						conectTarefa.setClausula(" WHERE CDTAREFA="+cdE[i]);
//						conectTarefa.update(" CDREFERENCIA="+cdRef);
						Log.i(LOG, cdTarefa+" inserido no servidor");
					}					
				}
			}

		}
		//Seleciona apenas tarefas que ainda n�o foram adicionadas no servidor
		else if(!s.equals("")){
			int codigoServidor = Integer.parseInt(s);
			int ultimoCdCelular = Integer.parseInt(pegaUltimo(" CDTAREFA ", cdU));
			if(ultimoCdCelular != -1)
			if(ultimoCdCelular>codigoServidor){
				String  cdTarefa, descricao, dest, responsavel, status, cdRef;
				conectTarefa.setOrder("");
				conectTarefa.setClausula(" WHERE CDTAREFA>"+codigoServidor);
				String[] cdE = MyString.tStringArray(conectTarefa.select(" CDTAREFA "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectTarefa.setClausula(" WHERE CDTAREFA="+cdE[i]);
					
					
					cdTarefa = MyString.tString(conectTarefa.select("CDTAREFA"));
					descricao = MyString.tString(conectTarefa.select("NMDESCRICAO"));
					descricao = URLEncoder.encode(descricao, "UTF-8");
					dest = MyString.tString3(conectTarefa.select("NMDESTINATARIOS"));
					Log.i(LOG, "destinatarios:"+dest);
					responsavel = MyString.tString(conectTarefa.select("CDRESPONSAVEL"));
					responsavel = URLEncoder.encode(responsavel, "UTF-8");
					status = MyString.tString(conectTarefa.select("CDSTATUS"));	
					cdRef = MyString.tString(conectTarefa.select("CDREFERENCIA"));	
							
					destinatarios = getNmDestinatarios(dest);
					
					for(int x=0; x<destinatarios.size(); x++){
						String d = URLEncoder.encode(destinatarios.get(x), "UTF-8");
						String campos = "descricao="+descricao+"&destinatario="+d+"&responsavel="+responsavel+"&status="+status+"&cdRef="+cdRef;
						Log.i(LOG, campos);
						insereServidor(url, campos);	

						Log.i(LOG, cdTarefa+" inserido no servidor");
					}
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
		
		String aux = exec.respServer.substring(0, exec.respServer.indexOf("$"));
		
		if(aux.equals("")){
			Log.i(LOG, "respServer == vazio"+aux);
		}
		else{
			cdRef = aux;
		}
		return cdRef;
		
	}
	
//	public void updateCelular(String url) throws InterruptedException{	
//		String cdU = userAtivo();
//		
//		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sa&cdU="+cdU;
//		ResponseHandler<String> handler = new BasicResponseHandler();
//		HttpClient client = new DefaultHttpClient();
//		HttpGet httpGet = new HttpGet("http://"+url+dados);
//		Log.i(LOG,"http://"+url+dados);
//		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
//		
//		exec.start();
//		
//		do{
////			Log.i(LOG,"sleep");
//			Thread.sleep(1000);
//		}
//		while(exec.respServer.equals(""));
//		
//		String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));
//		
//		if(aux.equals("")){
//			Log.i(LOG, "respServer == "+aux);
//		}
//		else{
//			String[] campos = MyString.montaUpdateAgenda(aux);
//			cod = MyString.getCod();
//			
//			int j=0;
//			boolean notificacao = false;
//			for(String i : campos){		
//				Log.i(LOG, "campos:"+i);
//				if(i.contains("STATUS='1'") & !notificacao){
//					notificacao = true;
//					geraNotificacaoEventosBaixados();
//				}
//				conectAgenda.setOrder("");
//				conectAgenda.setClausula(" WHERE CDEVENTO="+cod[j]);
//				conectAgenda.update(i);
//				Log.i(LOG, cod[j]+" atualizado no celular");
//				j++;
//			}
//			
//		}
//		
//	}

	/**
	 * 
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
//			Log.i(LOG,"sleep");
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
		return exec.respServer;
	}
	
	
	public void geraNotificacaoNovoEvento(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Tarefas.class), "Novas Tarefas Adicionadas", "Eventos", "Voce tem novos eventos em sua agenda");
	}
	
	
	public void geraNotificacaoEventosBaixados(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Tarefas.class), "Novos eventos baixados", "Eventos", "Eventos foram baixados em sua agenda");
	}
	
	
	public void gerarNotificacao(Context context, Intent intent, CharSequence ticker, CharSequence titulo, CharSequence descricao){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker(ticker);
		builder.setContentTitle(titulo);
		builder.setContentText(descricao);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
		builder.setContentIntent(p);
		
		Notification n = builder.build();
		n.vibrate = new long[]{150, 300, 150, 600};
		n.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(R.drawable.ic_launcher, n);
		
		try{
			Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone toque = RingtoneManager.getRingtone(context, som);
			toque.play();
		}
		catch(Exception e){}
	}
	
	
	public List<String> getNmDestinatarios(String string){
		List<String> lista = new ArrayList<String>();
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
		Log.i(LOG, "pegaUltimo()");
		conectTarefa.setClausula(" WHERE CDRESPONSAVEL="+cdU);
		conectTarefa.setOrder(" ORDER BY "+campo);		
		String[] aux = MyString.tStringArray(conectTarefa.select(campo));
		
		if(aux.length>0)
			return MyString.tiraEspa�o(aux[aux.length-1]);
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
//			Log.i(LOG,"sleep");
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
	}
	
	
	@Override
	public void onDestroy(){
		pendencia =  false;
		super.onDestroy();
		Log.i(LOG,"onDestroy() TAREFA");
	}
	
	
	public boolean getPendencia(){
		return this.pendencia;
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
