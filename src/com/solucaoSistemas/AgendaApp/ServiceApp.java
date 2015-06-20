/**@author maxissuel*/
package com.solucaoSistemas.AgendaApp;


import java.io.IOException;
import java.io.InputStreamReader;
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
			pendencia = ativo = true;

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
	 * caso houver altera�oes, realizar os respectivos updates
	 * deleta do servidor o que foi deletado no celular
	 * @throws InterruptedException 
	 * */
	public void  monitor() throws InterruptedException{
		Log.i(LOG, "entrou monitor()");
		Conexao conexao = new Conexao();
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
		else
			Log.i(LOG, "N�o Conectado");
		Log.i(LOG, "saiu monitor()");
	}
	
	public void geraNotificacaoNovoEvento(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Principal.class), "Novos eventos adicionados", "Eventos", "Voce tem novos eventos em sua agenda");
	}
	
	public void geraNotificacaoEventosBaixados(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Principal.class), "Novos eventos baixados", "Eventos", "Eventos foram baixados em sua agenda");
	}
	
	public void updateServidor(String url) throws InterruptedException{	
		String cdU = userAtivo();
		String[] cdE;				

		conectLogAgenda.setOrder("");
		conectLogAgenda.setClausula(" WHERE OPERACAO='U' ");
		cdE = MyString.tStringArray(conectLogAgenda.select(" CDEVENTOEXT "));
		String  cdEvento, desc, lc, obs, dt, hI, hF, st;
		
		if(cdE.length!=0){
		for(int j=0; j<cdE.length; j++){
			if(!cdE[j].equals("null")){
			Log.i(LOG, "cdE[j]"+cdE[j]);		
			conectAgenda.setOrder("");
			conectAgenda.setClausula(" WHERE CDEVENTOEXT="+cdE[j]);	
			
			cdEvento = MainActivity.tString(conectAgenda.select("CDEVENTO"));
			desc = MainActivity.tString(conectAgenda.select("DESCRICAO"));
			desc = URLEncoder.encode(desc);
			lc = MainActivity.tString(conectAgenda.select("LOCAL"));
			lc = URLEncoder.encode(lc);
			obs = MainActivity.tString(conectAgenda.select("OBSERVACAO"));
			obs = URLEncoder.encode(obs);
			dt = MainActivity.tString(conectAgenda.select("DATA"));
			dt = dt.replace( "\\" , ""); 
			hI = MainActivity.tString(conectAgenda.select("HORAINICIO"));
			String aux = hI.substring(0, 2) +":";
			aux += hI.substring(2, 4);
			hI = aux;
			hF = MainActivity.tString(conectAgenda.select("HORAFIM"));
			aux = hF.substring(0, 2) +":";
			aux += hF.substring(2, 4);
			hF = aux;
			st = MainActivity.tString(conectAgenda.select("STATUS"));					
			
			String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=u&cdU="+cdU+"&cdE="+cdE[j]+"&desc="+desc+"&obs="+obs+"&st="+st+"&dt="+dt+"&hI="+hI+"&hF="+hF+"&lc="+lc;
			ResponseHandler<String> handler = new BasicResponseHandler();
			HttpClient client = new DefaultHttpClient();
			Log.i(LOG,"HttpClient client = new DefaultHttpClient();");
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
		cdE = MainActivity.tStringArray(conectLogAgenda.select(" CDEVENTOEXT "));
		String cdExt="";
		
		if(cdE.length!=0){
		for(int j=0; j<cdE.length; j++){
			Log.i(LOG, "cdE[j]"+cdE[j]);
			if(!cdE[j].equals("null")){
				if(j == cdE.length-1)
					cdExt += MainActivity.tiraEspa�o(cdE[j]);
				else
					cdExt += MainActivity.tiraEspa�o(cdE[j])+","; 
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
		String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));

		if(aux.equals("")){
			Log.i(LOG, "respServer == "+aux);
		}
		else{
			String[] campos = MyString.tStringArray(aux);
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
		String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));
		
		
		if(aux.equals("")){
			Log.i(LOG, "respServer == "+aux);
		}
		else{
			try {
				String[] campos = MyString.montaInsertAgenda(aux);
				int j = 0;
				for(String i : campos){
					conectAgenda.insert(i);
					conectAgenda.setClausula(" WHERE CDEVENTOEXT="+cod[j]);
					Log.i(LOG, MyString.tString(conectAgenda.select(" CDEVENTO "))+" inserido no celular");
					updateCodServidor(MyString.tString(conectAgenda.select(" CDEVENTO ")), cod[j]);
					j++;
				}
				geraNotificacaoNovoEvento();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void selectCelular(String url) throws InterruptedException{	
		String cdU = userAtivo();
		
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sc&cdU="+cdU;
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://"+url+dados);
		Log.i(LOG,"http://"+url+dados);
		ExecutaWeb exec = new ExecutaWeb(handler, client, httpGet);
		
		exec.start();
		Log.i(LOG, "depois do exec.start");
		do{
//			Log.i(LOG,"sleep");
			Thread.sleep(1000);
		}
		while(exec.respServer.equals(""));
		Log.i(LOG, "antes do if");
		String s = exec.respServer.substring(0, exec.respServer.indexOf("$"));
		Log.i(LOG, "respServer:'"+s+"'");
		
		
		if(s.equals("")){
			Log.i(LOG, "respServer == "+s);
			
			int ultimoCdCelular = Integer.parseInt(pegaUltimo(" CDEVENTO ", cdU));
			Log.i(LOG, "ultimoCdCelular"+ultimoCdCelular);			
			
			if(ultimoCdCelular!=-1){
				String  cdEvento, desc, lc, obs, dt, hI, hF, st;
				conectAgenda.setOrder("");
				conectAgenda.setClausula("");
				String[] cdE = MyString.tStringArray(conectAgenda.select(" CDEVENTO "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectAgenda.setClausula(" WHERE CDEVENTO="+cdE[i]);
					
					cdEvento = MainActivity.tString(conectAgenda.select("CDEVENTO"));
					desc = MainActivity.tString(conectAgenda.select("DESCRICAO"));
					desc = URLEncoder.encode(desc);
					lc = MainActivity.tString(conectAgenda.select("LOCAL"));
					lc = URLEncoder.encode(lc);
					obs = MainActivity.tString(conectAgenda.select("OBSERVACAO"));
					obs = URLEncoder.encode(obs);
					dt = MainActivity.tString(conectAgenda.select("DATA"));
					dt = dt.replace( "\\" , ""); 
					hI = MainActivity.tString(conectAgenda.select("HORAINICIO"));
					String aux = hI.substring(0, 2) +":";
					aux += hI.substring(2, 4);
					hI = aux;
					hF = MainActivity.tString(conectAgenda.select("HORAFIM"));
					aux = hF.substring(0, 2) +":";
					aux += hF.substring(2, 4);
					hF = aux;
					st = MainActivity.tString(conectAgenda.select("STATUS"));	
					
					String campos = "cdU="+cdU+"&cdExt="+cdEvento+"&descricao="+desc+"&obs="+obs+"&status="+st+
							"&data="+dt+"&horaI="+hI+"&horaF="+hF+"&local="+lc;
					Log.i(LOG, campos);
					
					String cdExt = insereServidor(url, campos);
					Log.i(LOG, cdEvento+" inserido no servidor");
					conectAgenda.update(" CDEVENTOEXT="+cdExt);
					
				}
			}

		}
		else if(!s.equals("")){
			int codigoServidor = Integer.parseInt(s);
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
					
					cdEvento = MainActivity.tString(conectAgenda.select("CDEVENTO"));
					desc = MainActivity.tString(conectAgenda.select("DESCRICAO"));
					desc = URLEncoder.encode(desc);
					lc = MainActivity.tString(conectAgenda.select("LOCAL"));
					lc = URLEncoder.encode(lc);
					obs = MainActivity.tString(conectAgenda.select("OBSERVACAO"));
					obs = URLEncoder.encode(obs);
					dt = MainActivity.tString(conectAgenda.select("DATA"));
					dt = dt.replace( "\\" , ""); 
					hI = MainActivity.tString(conectAgenda.select("HORAINICIO"));
					String aux = hI.substring(0, 2) +":";
					aux += hI.substring(2, 4);
					hI = aux;
					hF = MainActivity.tString(conectAgenda.select("HORAFIM"));
					aux = hF.substring(0, 2) +":";
					aux += hF.substring(2, 4);
					hF = aux;
					st = MainActivity.tString(conectAgenda.select("STATUS"));	
					
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
		
		String aux = exec.respServer.substring(0, exec.respServer.indexOf("$"));
		
		if(aux.equals("")){
			Log.i(LOG, "respServer == vazio"+aux);
		}
		else{
			cdExt = aux;
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
		
		String aux = exec.respServer.substring(0, exec.respServer.indexOf("#"));
		
		if(aux.equals("")){
			Log.i(LOG, "respServer == "+aux);
		}
		else{
			String[] campos = MyString.montaUpdateAgenda(aux);
			
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
//		conectAgenda.setClausula(" WHERE CDEVENTO=79 ");
		conectAgenda.setOrder(" ORDER BY "+campo);		
		String[] aux = MyString.tStringArray(conectAgenda.select(campo));
		if(aux.length>0)
			return MainActivity.tiraEspa�o(aux[aux.length-1]);
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
		Conexao conexao = new Conexao();
		String url = conexao.pegaLink();
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
	
	
	class Conexao{
		
		public boolean isConected(){
			Log.i(LOG, "isConected()");
			ConnectivityManager c = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			if(c != null
					&&((c.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
					(c.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))
					){
				return true;
			}
			return false;
		}
		
		public boolean isWifi(){
			ConnectivityManager c = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			
			if(c != null
					&&((c.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED))){
				return true;				
			}
			return false;
		}
		
		public boolean isLocal(String url){
			try{
				Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 "+url);
				process.waitFor();
				InputStreamReader reader = new InputStreamReader(process.getInputStream());
				
				int i;
				char[] buffer = new char[4096];
				StringBuffer output = new StringBuffer();
				
				while((i = reader.read(buffer)) > 0){
					output.append(buffer, 0, i);
				}
				reader.close();
				String out = output.toString();
				if(out.contains("100%")){
					return false;
				}
				
			}
			catch(Exception e){
				Log.i(LOG,"error"+e);
				onDestroy();
			}
			return true;			
		}
		
		public String pegaLink(){
			Log.i(LOG, "entrou pegalink()");
			String url = "192.168.1.200";
			String urlAcesso = "";
			
			if(isConected()){
				Log.i(LOG, "conectado");

				if(isWifi()){
					if(isLocal(url)){
						urlAcesso = "192.168.1.200:5420";
					}
					else{
						urlAcesso = "solucaosistemas.dyndns-ip.com:5420";
					}
				}
				else{
					urlAcesso = "solucaosistemas.dyndns-ip.com:5420";
				}
			}
			else{
				Log.i(LOG, "nao conectado");
			}
			Log.i(LOG, "saiu pegalink()");
			Log.i(LOG, "");
			return urlAcesso;		}
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
	
	
	public static class MyString {
		public static String retiraQuebraLinha(String string){
			if(!string.equals("")){
//				Log.i(LOG, "retiraQuebraLinha("+string+")");
//				String parte1 = string.substring(0, string.indexOf('\\'));
//				Log.i(LOG, parte1);
//				string = parte1+"%5C"+string.substring(string.indexOf('\\')+1, string.length()-1);
//				Log.i(LOG, string);
				string.replace("\\n|\\r|\n|\r", "%5Cn");
				Log.i(LOG, "string: "+string);
			}
			return string;
		}
		
		public static String[] tStringArrayAgenda(Object string){
			   
			   String resul = string.toString();
			   char[] aux = new char[resul.length()];
			   
			   for(int i = 0; i < resul.length(); i++){
			    aux[i] = resul.charAt(i);
			   }
			   
			   resul = "";
			   
			   int j = 0;
			   for(int i = 0; i < aux.length; i++){
			    if(aux[i] == '$'){
			     j++;
			    }
			   }
			   
			   String[] a = new String[j*10];
			   j = 0;
			   for(int i = 0; i < aux.length; i++){
			    if(aux[i] == '$'){
//			     a[j] = resul;
//			     j++;
//			     resul = "";
			    }
			    
			    else if(aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' ||aux[i] == ','){
			     
			    }
			    
			    else if( aux[i] == '�'){
			     a[j] = resul;
			     j++;
			     resul = "";
			    }
			    
			    else{
			     resul += aux[i];
			    }
			   }
			   
			   return a;
			  }
		
		/**recebe um objeto e retorna em uma array de strings*/
		public static String[] tStringArray(Object string){
			
			String resul = string.toString();
			char[] aux = new char[resul.length()];
			
			for(int i = 0; i < resul.length(); i++){
				aux[i] = resul.charAt(i);
			}
			
			resul = "";
			
			int j = 0;
			for(int i = 0; i < aux.length; i++){
				if(aux[i] == '$'){
					j++;
				}
			}
			
			String[] a = new String[j];
			j = 0;
			for(int i = 0; i < aux.length; i++){
				if(aux[i] == '$'){
					a[j] = resul;
					j++;
					resul = "";
				}
				
				else if(aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ',' || aux[i] == ':'){
					
				}
				
				else{
					resul += aux[i];
				}
			}
			
			return a;
		}
		
		/**Retorna a ultima posi��o de uma array de strings que vem em formato Object
		 * @author maxissuel
		 * @params string*/	
		public static String tString2(Object string){
			
			String resul = string.toString();
			char[] aux = new char[resul.length()];
			String[] r = new String[1];
			
			for(int i = 0; i < resul.length(); i++){
				aux[i] = resul.charAt(i);
			}
			
			resul = "";
			
			for(int i = 0; i < aux.length; i++){
				
				if(aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ',' || aux[i] == ':'){
					
				}
				else{
					resul += aux[i];
				}
			}
			
			aux = new char[resul.length()];
			
			for(int i = 0; i < resul.length(); i++){
				aux[i] = resul.charAt(i);
			}
			
			for(int i = 0; i < aux.length; i++){
				if(aux[i] == ' ' ){
					
				}
				else if(aux[i] == '$'){
					r[0] = resul;
					resul = "";
				}
				else{
					resul += aux[i];
				}
			}
			
			return r[0];
		}
		
		/**retira os espa�os das primeiras posi�oes, ideal para as consultas do banco*/ 
		public static String tiraEspa�o(String string){
			String resp = "";

			for(int i = 0; i < string.length(); i++){
				if(string.charAt(0) == ' ' && i == 0){
				}
				else{
					resp += string.charAt(i);
				}
			}
			
			return resp;
		}
		
		/**retira todos os caracteres que a consulta sqlite retorna, e retorna em uma string s�*/
		public static String tString(Object string){
			
			String resul = string.toString();
			char[] aux = new char[resul.length()];
			
			for(int i = 0; i < resul.length(); i++){
				aux[i] = resul.charAt(i);
			}
			
			resul = "";
			
			for(int i = 0; i < aux.length; i++){
				
				if(aux[i] == '$' || aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ',' || aux[i] == ':'){
					
				}
				else{
					resul += aux[i];
				}
			}
			
			
			return resul;
		}

		public static String[] montaInsertAgenda(String resultGet){
			int x = 0;
			int c = 0;
			char[] aux = new char[resultGet.length()];
			String nm = "'";
		
			for(int i = 0; i < resultGet.length(); i++){
				aux[i] = resultGet.charAt(i);
				if(aux[i] == '$'){
					x++;
				}
			}
		
			int j = 0;
			cod = new String[x];
			String[] re = new String[x];
			String [] r = new String[11];
			
			for(int i = 0; i < aux.length; i++){
				if(aux[i] == '�'){
					nm += "'";
					r[c] = nm;
					nm = "'";
					c++;
				}
				else if(aux[i] == '$'){
					nm += "'";
					r[c] = nm;
					nm = "'";
					c = 0;
					re[j] = ordena(r, j);
					j++;
				}
				else{
					nm += aux[i];
				}
			}
			
		return re;
	}
		
		public static String[] montaUpdateAgenda(String resultGet){
			int x = 0;
			int c = 0;
			char[] aux = new char[resultGet.length()];
			String nm = "'";
		
			for(int i = 0; i < resultGet.length(); i++){
				aux[i] = resultGet.charAt(i);
				if(aux[i] == '$'){
					x++;
				}
			}
		
			int j = 0;
			cod = new String[x];
			String[] re = new String[x];
			String [] r = new String[11];
			
			for(int i = 0; i < aux.length; i++){
				if(aux[i] == '�'){
					nm += "'";
					r[c] = nm;
					nm = "'";
					c++;
				}
				else if(aux[i] == '$'){
					nm += "'";
					r[c] = nm;
					nm = "'";
					c = 0;
					re[j] = ordena2(r, j);
					j++;
				}
				else{
					nm += aux[i];
				}
			}
			
		return re;
	}
		
	private static String ordena2(String[] array, int j){
			String retorno = "";
			cod[j] = array[8];
			retorno += "DESCRICAO="+array[2]+",LOCAL="+array[9]+",OBSERVACAO="+array[3]+",DATA="+array[4]+",HORAINICIO="+array[5]+",HORAFIM="+array[6]+",STATUS="+array[7];
			return retorno;
		}
		
	private static String ordena(String[] array, int j){
		String retorno = "";
		cod[j] = array[0].replace("'" , "");
		retorno += "null,"+array[0].replace("'","")+","+array[1]+","+array[2]+","+array[9]+","+array[3]+","+array[4]+","+array[5]+","+array[6]+","+array[7];
		return retorno;
	}
}
	
	class  ExecutaWeb extends Thread{
		ResponseHandler<String> handler;
		HttpClient client;
		HttpGet httpGet;
		String url;
		String respServer;
		
		public ExecutaWeb(ResponseHandler<String> handler, HttpClient client,			
			HttpGet httpGet) {
			this.handler = handler;
			this.client = client;
			this.httpGet = httpGet;
			this.respServer = "";
		}
		
		@Override
		public void run(){
			try {
				respServer = client.execute(httpGet, handler);				
				Log.i(LOG, "run: '"+respServer+"'");
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Log.i(LOG, "Erro"+e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(LOG, "Erro"+e);
			}
		}

		public String getRespServer(){
			return respServer;
		}
	}
	
	class Cod{
		String cod;
	}
}
	

