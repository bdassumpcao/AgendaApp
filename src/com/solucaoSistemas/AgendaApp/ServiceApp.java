/**@author maxissuel*/
package com.solucaoSistemas.AgendaApp;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ServiceApp extends Service {
	public boolean pendencia = false;
	public boolean exec = true;
	final public boolean statusServico = true;
	ConectaLocal conectAgenda;
	ConectaLocal conectUser;
	ConectaLocal conectLogAgenda;
	static String[] cod;
	static boolean ativo = false;
	private static  String LOG = "teste";
	/** Para a normalização dos caracteres de 32 a 255, primeiro caracter */  
	private static final char[] FIRST_CHAR =  
		    (" !'#$%&'()*+\\-./0123456789:;<->?@ABCDEFGHIJKLMNOPQRSTUVWXYZ"  
		        + "[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ E ,f'.++^%S<O Z  ''''.-"  
		        + "-~Ts>o ZY !C#$Y|$'(a<--(_o+23'u .,1o>113?AAAAAAACEEEEIIIIDNOO"  
		        + "OOOXOUUUUyTsaaaaaaaceeeeiiiidnooooo/ouuuuyty")  
		        .toCharArray();  
	/** Para a normalização dos caracteres de 32 a 255, segundo caracter */  
	private static final char[] SECOND_CHAR =  
		    ("  '         ,                                               "  
		        + "\\                                   $  r'. + o  E      ''  "  
		        + "  M  e     #  =  'C.<  R .-..     ..>424     E E            "  
		        + "   E E     hs    e e         h     e e     h ")  
		        .toCharArray(); 
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.i(LOG, "onCreate()");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i(LOG, "onStartCommand()");
		
		if(!pendencia){
			pendencia = true;
			ativo = true;

				try{
					conectAgenda = new ConectaLocal(this, "AGENDA");
					conectUser = new ConectaLocal(this, "USUARIO");
					conectLogAgenda = new ConectaLocal(getApplicationContext(), "LOGAGENDA");
					monitor();
				}catch(Exception e){
					Log.i(LOG, "erro no monitor\n"+e);
				}
		}
		
		onDestroy();
		
		return(START_STICKY);
	}
	
	@Override
	public void onDestroy(){
		pendencia = ativo = false;
		super.onDestroy();
		Log.i(LOG,"onDestroy()");
	}
	
	public boolean getPendencia(){
		return this.pendencia;
	}
	
	/**verifica no servidor se tem algo a mais
	 * verifica no banco do cel se tem algo a mais
	 * caso houver alteraçoes, realizar os respectivos updates
	 * deleta do servidor o que foi deletado no celular
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 * */
	public void  monitor() throws InterruptedException, UnsupportedEncodingException{
		Log.i(LOG, "entrou monitor()");
		Conexao conexao = new Conexao(this);
		String url = "";
		
		if(conexao.isConected()){
			url = conexao.pegaLink();
			Log.i(LOG, "link:\n"+url);
			
			//----------------------------------
			
			Log.i(LOG,"entrou deleteServidor()");
			deleteServidor(url);
			Log.i(LOG,"saiu deleteServidor()");
			Log.i(LOG, "");
			
			Log.i(LOG,"entrou deleteCelular()");
			deleteCelular(url);
			Log.i(LOG,"saiu deleteCelular()");
			Log.i(LOG, "");
			
			Log.i(LOG,"entrou selectCelular()");
			selectCelular(url);
			Log.i(LOG,"saiu selectCelular()");
			Log.i(LOG, "");
			
			Log.i(LOG,"entrou selectServidor()");
			selectServidor(url);
			Log.i(LOG,"saiu selectServidor()");
			Log.i(LOG, "");
			
			Log.i(LOG,"entrou updateServidor()");
			updateServidor(url);
			Log.i(LOG,"saiu updateServidor()");
			Log.i(LOG, "");
			
			Log.i(LOG,"entrou updateCelular()");
			updateCelular(url);		
			Log.i(LOG,"saiu updateCelular()");
			Log.i(LOG, "");


		}
		else{
			Log.i(LOG, "Não Conectado");
		}
		Log.i(LOG, "saiu monitor()");
	}
	
	public void geraNotificacaoNovoEvento(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Principal.class), "Novos eventos adicionados", "Eventos", "Voce tem novos eventos em sua agenda");
	}
	
	public void geraNotificacaoEventosBaixados(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Principal.class), "Novos eventos baixados", "Eventos", "Eventos foram baixados em sua agenda");
	}
	
	public void updateServidor(String url) throws InterruptedException, UnsupportedEncodingException{	
		String cdU = userAtivo();
		String[] cdE;				

		conectLogAgenda.setOrder("");
		conectLogAgenda.setClausula(" WHERE OPERACAO='U' ");
		cdE = MyString.tStringArray(conectLogAgenda.select(" CDEVENTOEXT "));
//		String  cdEvento;
		String desc, lc, obs, dt, hI, hF, st;
		
		if(cdE.length!=0){
		for(int j=0; j<cdE.length; j++){
			if(!cdE[j].equals("null")){
			Log.i(LOG, "cdE[j]"+cdE[j]);		
			conectAgenda.setOrder("");
			conectAgenda.setClausula(" WHERE CDEVENTOEXT="+cdE[j]);	
			
//			cdEvento = MainActivity.tString(conectAgenda.select("CDEVENTO"));
			desc = MyString.tString(conectAgenda.select("DESCRICAO"));
			desc = URLEncoder.encode(desc, "UTF-8");
			lc = MyString.tString(conectAgenda.select("LOCAL"));
			lc = URLEncoder.encode(lc, "UTF-8");
			obs = MyString.tString(conectAgenda.select("OBSERVACAO"));
			obs = URLEncoder.encode(obs, "UTF-8");
			dt = MyString.tString(conectAgenda.select("DATA"));
			dt = dt.replace( "\\" , ""); 
			hI = MyString.tString(conectAgenda.select("HORAINICIO"));
			String aux = hI.substring(0, 2) +":";
			aux += hI.substring(2, 4);
			hI = aux;
			hF = MyString.tString(conectAgenda.select("HORAFIM"));
			aux = hF.substring(0, 2) +":";
			aux += hF.substring(2, 4);
			hF = aux;
			st = MyString.tString(conectAgenda.select("STATUS"));					
			
			String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=u&cdU="+cdU+"&cdE="+cdE[j]+"&desc="+desc+"&obs="+obs+"&st="+st+"&dt="+dt+"&hI="+hI+"&hF="+hF+"&lc="+lc;
			ResponseHandler<String> handler = new BasicResponseHandler();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://"+url+dados);	
			Log.i(LOG,"http://"+url+dados);
			ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);			
			
			exec.start();
			
			do{
//				Log.i(LOG,"sleep");
				Thread.sleep(1000);
			}
			while(exec.respServer.equals(""));
			
			if(!exec.respServer.equals("$")){
				Log.i(LOG, "respServer == "+exec.respServer);
			}
			else{
				conectLogAgenda.setOrder("");
				conectLogAgenda.setClausula(" WHERE CDEVENTOEXT="+cdE[j]);
				conectLogAgenda.delete();
				Log.i(LOG, cdE[j]+" alterado no servidor");
			}
			}
		}
		}
		
	}
	
	public void deleteServidor(String url) throws InterruptedException{	
		String cdU = userAtivo();
		String[] cdE;				

		conectLogAgenda.setOrder("");
		conectLogAgenda.setClausula(" WHERE OPERACAO='D' ");
		cdE = MyString.tStringArray(conectLogAgenda.select(" CDEVENTOEXT "));
		String cdExt="";
		
		if(cdE.length!=0){
		for(int j=0; j<cdE.length; j++){
			Log.i(LOG, "cdE[j]"+cdE[j]);
			if(!cdE[j].equals("null")){
				if(j == cdE.length-1)
					cdExt += MyString.tiraEspaço(cdE[j]);
				else
					cdExt += MyString.tiraEspaço(cdE[j])+","; 
			}
		}

			String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=d&cdU="+cdU+"&cdE="+cdExt;
			ResponseHandler<String> handler = new BasicResponseHandler();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://"+url+dados);
			Log.i(LOG,"http://"+url+dados);
			ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
			
			exec.start();
			
			do{
//				Log.i(LOG,"sleep");
				Thread.sleep(1000);
			}
			while(exec.respServer.equals(""));
			
			if(!exec.respServer.equals("$")){
				Log.i(LOG, "respServer == "+exec.respServer);
			}
			else{
				conectLogAgenda.setOrder("");
				conectLogAgenda.setClausula(" WHERE OPERACAO='D' ");
				conectLogAgenda.delete();
				Log.i(LOG, cdExt+" excluido no servidor!");
			}
		}
		
	}
	
	
	public void deleteCelular(String url) throws InterruptedException{	
		String cdU = userAtivo();	

		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sd&cdU="+cdU;
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://"+url+dados);
		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
			
		exec.start();
			
		do{
//			Log.i(LOG,"sleep");
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
		String respServer = exec.respServer.substring(0, exec.respServer.indexOf("#"));
		respServer = normalize(respServer);
				
		if(respServer.equals("")){
			Log.i(LOG, "respServer == "+respServer);
		}
		else{
			String[] campos = MyString.tStringArray(respServer);
			for(int i=0 ;i<campos.length ;i++){		
					conectAgenda.setOrder("");
					conectAgenda.setClausula(" WHERE CDEVENTOEXT="+campos[i]);
					conectAgenda.delete();
					Log.i(LOG, campos[i]+" excluido no celular");
			}			
		}
	}	
	
	//http://192.168.1.200:5420/webservice/processo.php?flag=3&chave=l33cou&operacao=i&cdU=1&cdExt=1&descricao=het&obs=teste&status=a&data=10/05/15&horaI=15:06&horaF=20:00&local=teste
	public void selectServidor(String url) throws InterruptedException{	
		String cdU = userAtivo();
		String cdExt = pegaUltimo(" CDEVENTOEXT ", cdU);
		cdExt = normalize(cdExt);
		String dados = "";
		if(cdExt.equals("-1")){
			dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sall&cdU="+cdU;
		}
		
		if(!cdExt.equals("-1")){
			dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=su&cdU="+cdU+"&cdE="+cdExt;
		}

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
		String respServer = exec.respServer.substring(0, exec.respServer.indexOf("#"));
//		respServer = normalize(respServer);
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == "+respServer);
		}
		else{
			try {
				String[] campos = MyString.montaInsertAgenda(respServer);
				cod = MyString.getCod();
				
				int j = 0;
				for(String i : campos){
					Log.i(LOG, "i:"+i);
					conectAgenda.insert(i);
					conectAgenda.setClausula(" WHERE CDEVENTOEXT='"+cod[j]+"'");
					Log.i(LOG, " WHERE CDEVENTOEXT="+cod[j]);
					Log.i(LOG, MyString.tString(conectAgenda.select(" CDEVENTO "))+" inserido no celular");
					Log.i(LOG, "updateCodServidor:"+MyString.tString(conectAgenda.select(" CDEVENTO ")));
					updateCodServidor(MyString.tString(conectAgenda.select(" CDEVENTO ")), cod[j]);
					
					j++;
				}
				geraNotificacaoNovoEvento();
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(LOG, e+"");
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void selectCelular(String url) throws InterruptedException, UnsupportedEncodingException{	
		String cdU = userAtivo();
		
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sc&cdU="+cdU;
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
		respServer = normalize(respServer);
		Log.i(LOG, "respServer:'"+respServer+"'");
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == "+respServer);
			
			int ultimoCdCelular = Integer.parseInt(pegaUltimo(" CDEVENTO ", cdU));
			Log.i(LOG, "ultimoCdCelular"+ultimoCdCelular);			
			
			//se for igual a menos um não executa o restante pois não tem eventos para inserir
			if(ultimoCdCelular!=-1){
				String  cdEvento, desc, lc, obs, dt, hI, hF, st;
				conectAgenda.setOrder("");
				conectAgenda.setClausula("");
				String[] cdE = MyString.tStringArray(conectAgenda.select(" CDEVENTO "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectAgenda.setClausula(" WHERE CDEVENTO="+cdE[i]);
					
					cdEvento = MyString.tString(conectAgenda.select("CDEVENTO"));
					desc = MyString.tString(conectAgenda.select("DESCRICAO"));
					desc = URLEncoder.encode(desc, "UTF-8");
					lc = MyString.tString(conectAgenda.select("LOCAL"));
					lc = URLEncoder.encode(lc, "UTF-8");
					obs = MyString.tString(conectAgenda.select("OBSERVACAO"));
					obs = URLEncoder.encode(obs, "UTF-8");
					dt = MyString.tString(conectAgenda.select("DATA"));
					dt = dt.replace( "\\" , ""); 
					hI = MyString.tString(conectAgenda.select("HORAINICIO"));
					String aux = hI.substring(0, 2) +":";
					aux += hI.substring(2, 4);
					hI = aux;
					hF = MyString.tString(conectAgenda.select("HORAFIM"));
					aux = hF.substring(0, 2) +":";
					aux += hF.substring(2, 4);
					hF = aux;
					st = MyString.tString(conectAgenda.select("STATUS"));	
					
					String campos = "cdU="+cdU+"&cdExt="+cdEvento+"&descricao="+desc+"&obs="+obs+"&status="+st+
							"&data="+dt+"&horaI="+hI+"&horaF="+hF+"&local="+lc;
					Log.i(LOG, campos);
					
					String cdExt = insereServidor(url, campos);
					Log.i(LOG, cdEvento+" inserido no servidor");
					conectAgenda.update(" CDEVENTOEXT="+cdExt);
					
				}
			}

		}
		else if(!respServer.equals("")){
			int codigoServidor = Integer.parseInt(respServer);
			int ultimoCdCelular = Integer.parseInt(pegaUltimo(" CDEVENTO ", cdU));
			if(ultimoCdCelular != -1)
			if(ultimoCdCelular>codigoServidor){
				String  cdEvento, desc, lc, obs, dt, hI, hF, st;
				conectAgenda.setOrder("");
				conectAgenda.setClausula(" WHERE CDEVENTO>"+codigoServidor);
				String[] cdE = MyString.tStringArray(conectAgenda.select(" CDEVENTO "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectAgenda.setClausula(" WHERE CDEVENTO="+cdE[i]);
					
					cdEvento = MyString.tString(conectAgenda.select("CDEVENTO"));
					desc = MyString.tString(conectAgenda.select("DESCRICAO"));
					desc = URLEncoder.encode(desc);
					lc = MyString.tString(conectAgenda.select("LOCAL"));
					lc = URLEncoder.encode(lc);
					obs = MyString.tString(conectAgenda.select("OBSERVACAO"));
					obs = URLEncoder.encode(obs);
					dt = MyString.tString(conectAgenda.select("DATA"));
					dt = dt.replace( "\\" , ""); 
					hI = MyString.tString(conectAgenda.select("HORAINICIO"));
					String aux = hI.substring(0, 2) +":";
					aux += hI.substring(2, 4);
					hI = aux;
					hF = MyString.tString(conectAgenda.select("HORAFIM"));
					aux = hF.substring(0, 2) +":";
					aux += hF.substring(2, 4);
					hF = aux;
					st = MyString.tString(conectAgenda.select("STATUS"));	
					
					String campos = "cdU="+cdU+"&cdExt="+cdEvento+"&descricao="+desc+"&obs="+obs+"&status="+st+
							"&data="+dt+"&horaI="+hI+"&horaF="+hF+"&local="+lc;
					Log.i(LOG, campos);
					
					String cdExt = insereServidor(url, campos);
					Log.i(LOG, cdEvento+" inserido no servidor");
					conectAgenda.update(" CDEVENTOEXT="+cdExt);
					
				}
			}
			
		}
		
	}
	

	
	public String insereServidor(String url, String campos) throws InterruptedException{	
		Log.i(LOG, "entrou insereServidor()");
		String cdExt = "";
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=i&"+campos;
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
		respServer = normalize(respServer);
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == vazio"+respServer);
		}
		else{
			cdExt = respServer;
		}
		return cdExt;
		
	}
	
	public void updateCelular(String url) throws InterruptedException{	
		String cdU = userAtivo();
		
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sa&cdU="+cdU;
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
		
		String respServer = exec.respServer.substring(0, exec.respServer.indexOf("#"));
		respServer = normalize(respServer);
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == "+respServer);
		}
		else{
			String[] campos = MyString.montaUpdateAgenda(respServer);
			cod = MyString.getCod();
			
			int j=0;
			boolean notificacao = false;
			for(String i : campos){		
				Log.i(LOG, "campos:"+i);
				if(i.contains("STATUS='1'") & !notificacao){
					notificacao = true;
					geraNotificacaoEventosBaixados();
				}
				conectAgenda.setOrder("");
				conectAgenda.setClausula(" WHERE CDEVENTO="+cod[j]);
				conectAgenda.update(i);
				Log.i(LOG, cod[j]+" atualizado no celular");
				j++;
			}
			
		}
		
	}
	
	public String pegaUltimo(String campo, String cdU){
		Log.i(LOG, "pegaUltimo()");
		conectAgenda.setClausula(" WHERE CDUSUARIO="+cdU);
		conectAgenda.setOrder(" ORDER BY "+campo);		
		String[] aux = MyString.tStringArray(conectAgenda.select(campo));
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
	
	public void insertServidor(String cdExt, String url){
		
	}
	
	public void updateCodServidor(String cdExt, String cdE) throws InterruptedException{
		Log.i(LOG, "updateCodServidor()");
		Conexao conexao = new Conexao(this);
		String url = conexao.pegaLink();
		cdE = normalize(cdE);
		Log.i(LOG, "cdE:"+cdE);
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=uc&cdE="+cdExt+"&cdExt="+cdE;
		
		
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
	
	public static String normalize(String str) {  
	    char[] chars = str.toCharArray();  
	    StringBuffer ret = new StringBuffer(chars.length * 2);  
	    for (int i = 0; i < chars.length; ++i) {  
	        char aChar = chars[i];  
	        if (aChar == ' ' || aChar == '\t') {  
	            ret.append(' '); // convertido para espaço  
	        } else if (aChar > ' ' && aChar < 256) {  
	            if (FIRST_CHAR[aChar - ' '] != ' ') {  
	                ret.append(FIRST_CHAR[aChar - ' ']); // 1 caracter  
	            }  
	            if (SECOND_CHAR[aChar - ' '] != ' ') {  
	                ret.append(SECOND_CHAR[aChar - ' ']); // 2 caracteres  
	            }  
	        }  
	    }  
	  
	    return ret.toString();  
	}  
}