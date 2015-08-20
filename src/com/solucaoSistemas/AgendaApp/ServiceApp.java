/**@author maxissuel*/
package com.solucaoSistemas.AgendaApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import Utilitarios.MyString;
import Web.Conexao;
import Web.ExecutaWeb;
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
import android.view.KeyEvent;

public class ServiceApp extends Service {
	public boolean pendencia = false;
	public boolean exec = true;
	final public boolean statusServico = true;
	static ConectaLocal conectAgenda;
	ConectaLocal conectUser;
	ConectaLocal conectLogAgenda;
	ArrayList<Thread> listaThread;
	static String[] cod;
	static boolean ativo = false;
	private static  String LOG = "teste";
	static Conexao conexao;
	int num;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		conexao = new Conexao(this);
		listaThread = new ArrayList<Thread>();
		Log.i(LOG, "onCreate() AGENDA");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i(LOG, "onStartCommand() AGENDA");
				
		if(!pendencia){
			pendencia = true;
			ativo = true;

				try{
					conectAgenda = new ConectaLocal(this, "AGENDA");
					conectUser = new ConectaLocal(this, "USUARIO");
					conectLogAgenda = new ConectaLocal(getApplicationContext(), "LOGAGENDA");
					monitor();
				}catch(Exception e){
					Log.i(LOG, "erro no monitor AGENDA\n"+e);
				}
		}
		
		onDestroy();
		
		return(START_STICKY);
	}
	
	@Override
	public void onDestroy(){
		pendencia = ativo = false;
		super.onDestroy();
		Log.i(LOG,"onDestroy() AGENDA");
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
		Log.i(LOG, "entrou monitor() AGENDA");
		Conexao conexao = new Conexao(this);
		
		if(conexao.isConected()){
			final String url = conexao.pegaLink();
			Log.i(LOG, "link:\n"+url);
			
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i(LOG,"entrou deleteServidor() AGENDA");
					try {
						deleteServidor(url);
					} catch (InterruptedException e) {
						Log.i(LOG, ""+e);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu deleteServidor() AGENDA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			
			listaThread.add(new Thread(new Runnable() {
								
				@Override
				public void run() {
					Log.i(LOG,"entrou updateServidor() AGENDA");
					try {
						updateServidor(url);
					} catch (UnsupportedEncodingException e) {
						Log.i(LOG, ""+e);
					} catch (InterruptedException e) {
						Log.i(LOG, ""+e);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu updateServidor() AGENDA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					Log.i(LOG,"entrou selectCelular() AGENDA");
					try {
						selectCelular(url);
					} catch (UnsupportedEncodingException e) {
						Log.i(LOG, ""+e);
					} catch (InterruptedException e) {
						Log.i(LOG, ""+e);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu selectCelular() AGENDA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			listaThread.add(new Thread(new Runnable() {
				
				@Override
				public void run() {
					conectAgenda.delete();
					Log.i(LOG,"Apagou dados da AGENDA");
					
					Log.i(LOG,"entrou selectServidor() AGENDA");
					try {
						selectServidor(url);
					} catch (InterruptedException e) {
						Log.i(LOG, ""+e);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i(LOG,"saiu selectServidor() AGENDA");
					Log.i(LOG, "");
					try {
						this.finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}));
			
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
		Log.i(LOG, "saiu monitor() AGENDA");
	}
	
	public void geraNotificacaoNovoEvento(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Principal.class), "Novos eventos adicionados", "Eventos", "Voce tem novos eventos em sua agenda");
	}
	
	@Deprecated
	public void geraNotificacaoEventosBaixados(){
		gerarNotificacao(getApplicationContext(), new Intent(getBaseContext(),Principal.class), "Eventos Alterados", "Eventos", "Eventos foram alterados em sua agenda");
	}
	
	public void updateServidor(String url) throws Throwable{	
		String cdU = userAtivo();
		String[] cdE;				

		conectLogAgenda.setOrder("");
		conectLogAgenda.setClausula(" WHERE OPERACAO='U' ");
		cdE = MyString.tStringArray(conectLogAgenda.select(" CDEVENTO "));
//		String  cdEvento;
		String cdEventoExt, desc, lc, obs, dt, hI, hF, st;
		
		if(cdE.length!=0){
			for(int j=0; j<cdE.length; j++){
				if(!cdE[j].equals("null")){
				Log.i(LOG, "cdE[j]"+cdE[j]);		
				conectAgenda.setOrder("");
				conectAgenda.setClausula(" WHERE CDEVENTO="+cdE[j]);	
				
				cdEventoExt = MyString.tiraEspaço(MyString.tString(conectAgenda.select("CDEVENTOEXT")));
				desc = MyString.tiraEspaço(MyString.tString(conectAgenda.select("DESCRICAO")));
				desc = URLEncoder.encode(desc, "UTF-8");
				lc = MyString.tiraEspaço(MyString.tString(conectAgenda.select("LOCAL")));
				lc = URLEncoder.encode(lc, "UTF-8");
				obs = MyString.tiraEspaço(MyString.tString(conectAgenda.select("OBSERVACAO")));
				obs = URLEncoder.encode(obs, "UTF-8");
				dt = MyString.tiraEspaço(MyString.tString(conectAgenda.select("DATA")));
				dt = dt.replace( "\\" , ""); 
				hI = MyString.tiraEspaço(MyString.tString(conectAgenda.select("HORAINICIO")));
				String aux = hI.substring(0, 2) +":";
				aux += hI.substring(2, 4);
				hI = aux;
				hF = MyString.tiraEspaço(MyString.tString(conectAgenda.select("HORAFIM")));
				aux = hF.substring(0, 2) +":";
				aux += hF.substring(2, 4);
				hF = aux;
				st = MyString.tiraEspaço(MyString.tString(conectAgenda.select("STATUS")));					
				
				String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=u&cdU="+cdU+"&cdExt="+cdEventoExt+"&desc="+desc+"&obs="+obs+"&st="+st+"&dt="+dt+"&hI="+hI+"&hF="+hF+"&lc="+lc;
				String respServer = webservice(url, dados);
				respServer = respServer.substring(0, respServer.indexOf("$"));
				
				
				if(respServer.equals("")){
					Log.i(LOG, "respServer == "+respServer);
				}
				else{
					conectLogAgenda.setOrder("");
					conectLogAgenda.setClausula(" WHERE CDEVENTO="+cdE[j]);
					conectLogAgenda.delete();
					Log.i(LOG, cdE[j]+" alterado no servidor");
				}
				}
			}
		}
		
	}
	
	public void deleteServidor(String url) throws Throwable{	
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

			String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=d&cdU="+cdU+"&cdExt="+cdExt;
			String respServer = webservice(url, dados);
			respServer = respServer.substring(0, respServer.indexOf("$"));
			respServer = MyString.normalize(respServer);
			
			if(!respServer.equals("")){
				Log.i(LOG, "respServer == "+respServer);
			}
			else{
				conectLogAgenda.setOrder("");
				conectLogAgenda.setClausula(" WHERE OPERACAO='D' ");
				conectLogAgenda.delete();
				Log.i(LOG, cdExt+" excluido no servidor!");
			}
		}
		
	}
	
	@Deprecated
	public void deleteCelular(String url) throws Throwable{	
		String cdU = userAtivo();	

		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sd&cdU="+cdU;
		String respServer = webservice(url, dados);
		respServer = respServer.substring(0, respServer.indexOf("#"));
		respServer = MyString.normalize(respServer);
				
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
	
	@SuppressWarnings("deprecation")
	public void selectCelular(String url) throws Throwable{
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
		while(MyString.normalize(exec.respServer).equals(""));
				
		String respServer = exec.respServer.substring(0, exec.respServer.indexOf("#"));
		respServer = MyString.normalize(respServer);
		Log.i(LOG, "respServer:'"+respServer+"'");
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == "+respServer);
			
			int ultimoCdCelular = Integer.parseInt(pegaUltimo(" CDEVENTO ", cdU));
			Log.i(LOG, "ultimoCdCelular"+ultimoCdCelular);			
			
			//se for igual a menos um não executa o restante pois não tem eventos para inserir
			if(ultimoCdCelular!=-1){
				String  cdEventoExt, desc, lc, obs, dt, hI, hF, st;
				conectAgenda.setOrder("");
				conectAgenda.setClausula("");
				String[] cdE = MyString.tStringArray(conectAgenda.select(" CDEVENTO "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectAgenda.setClausula(" WHERE CDEVENTO="+cdE[i]);
					
					cdEventoExt = MyString.tString(conectAgenda.select("CDEVENTOEXT"));
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
					
					String campos = "cdU="+cdU+"&cdExt="+cdEventoExt+"&descricao="+desc+"&obs="+obs+"&status="+st+
							"&data="+dt+"&horaI="+hI+"&horaF="+hF+"&local="+lc;
					Log.i(LOG, campos);
					
					insereServidor(url, campos);
					Log.i(LOG, cdEventoExt+" inserido no servidor");
//					conectAgenda.update(" CDEVENTOEXT="+cdExt);					
				}
			}
		}
		else if(!respServer.equals("")){
			int codigoServidor = Integer.parseInt(respServer);
			int ultimoCdCelular = Integer.parseInt(pegaUltimo(" CDEVENTOEXT ", cdU));
			if(ultimoCdCelular != -1)
			if(ultimoCdCelular>codigoServidor){
				String  cdEventoExt, desc, lc, obs, dt, hI, hF, st;
				conectAgenda.setOrder("");
				conectAgenda.setClausula(" WHERE CDEVENTOEXT>"+codigoServidor);
				String[] cdE = MyString.tStringArray(conectAgenda.select(" CDEVENTO "));
				for(int i=0; i<cdE.length; i++){
					Log.i(LOG, cdE[i]);
					conectAgenda.setClausula(" WHERE CDEVENTO="+cdE[i]);
					
					cdEventoExt = MyString.tString(conectAgenda.select("CDEVENTOEXT"));
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
					
					String campos = "cdU="+cdU+"&cdExt="+cdEventoExt+"&descricao="+desc+"&obs="+obs+"&status="+st+
							"&data="+dt+"&horaI="+hI+"&horaF="+hF+"&local="+lc;
					Log.i(LOG, campos);
					
					insereServidor(url, campos);
					Log.i(LOG, cdEventoExt+" inserido no servidor");
//					conectAgenda.update(" CDEVENTOEXT="+cdExt);					
				}
			}			
		}		
	}
	
	public String insereServidor(String url, String campos) throws Throwable{	
		Log.i(LOG, "entrou insereServidor()");
		String cdExt = "";
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=i&"+campos;

		String respServer = webservice(url, dados);
		respServer = respServer.substring(0, respServer.indexOf("$"));
		respServer = MyString.normalize(respServer);
		
		if(respServer.equals("")){
			Log.i(LOG, "respServer == vazio"+respServer);
		}
		else{
			cdExt = respServer;
		}
		return cdExt;		
	}
	
	//http://192.168.1.200:5420/webservice/processo.php?flag=3&chave=l33cou&operacao=i&cdU=1&cdExt=1&descricao=het&obs=teste&status=a&data=10/05/15&horaI=15:06&horaF=20:00&local=teste
	public void selectServidor(String url) throws Throwable{	
		String cdU = userAtivo();
		String dados = "";
		dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sall&cdU="+cdU;
		
		String respServer = webservice(url, dados);
		respServer = respServer.substring(0, respServer.indexOf("#"));
		
		if(MyString.normalize(respServer).equals("")){
			Log.i(LOG, "respServer == "+MyString.normalize(respServer));
		}
		else{
			try {
				
				montaInsertAgenda(respServer, cdU);				

			} catch (Exception e) {
				e.printStackTrace();
				Log.i(LOG, e+"");
			}
		}		
	}
	
	@Deprecated
	public void updateCelular(String url) throws Throwable{	
		String cdU = userAtivo();
		
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=sa&cdU="+cdU;
		String respServer = webservice(url, dados);		
		respServer = respServer.substring(0, respServer.indexOf("#"));
//		respServer = MyString.normalize(respServer);
		
		if(MyString.normalize(respServer).equals("")){
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
	
	/**
	 * 
	 * @param url
	 * @param dados
	 * @return exec.respServer
	 * @throws Throwable 
	 */
	public String webservice(String url, String dados) throws Throwable{
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
	
	public String pegaUltimo(String campo, String cdU){
		conectAgenda.setClausula(" WHERE CDUSUARIO="+cdU);	
		String[] aux = MyString.tStringArray(conectAgenda.select(campo));

		if(aux.length>0)
			return MyString.tiraEspaço(aux[aux.length-1]);
		else
			return "-1";
	}
	
	public static int pegaUltimoCdExt(){
		if(MyString.tString(conectAgenda.select(" MAX(CDEVENTOEXT) ")).equals("null"))
			return 0;
		else return Integer.parseInt(MyString.tString(conectAgenda.select(" MAX(CDEVENTOEXT) ")));
	}

	public String userAtivo(){
		Log.i(LOG, "userAtivo()");
		conectUser.setClausula(" WHERE STATUS=1 ");
		conectUser.setOrder(" ORDER BY CDUSUARIO ");
		return MyString.tString(conectUser.select(" CDUSUARIO "));
	}
	
	
	public static void updateCodServidor(String data, String horaInicio, String cdU, String cdExt) throws InterruptedException{
		Log.i(LOG, "updateCodServidor()");
		
		String url = conexao.pegaLink();
		cdExt = MyString.normalize(cdExt);
		Log.i(LOG, "cdExt:"+cdExt);
		String dados = "/webservice/processo.php?flag=3&chave=l33cou&operacao=uc&data="+data+"&horaInicio="+horaInicio+"&cdU="+cdU+"&cdExt="+cdExt;
		
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
	
	public static String[] montaInsertAgenda(String resultGet,String  cdU){
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
			if(aux[i] == '§'){
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
				re[j] = ordena(r, j, cdU);
				j++;
			}
			else{
				nm += aux[i];
			}
		}
		
		return re;
	}
	
	
	private static String ordena(String[] array, int j, String cdU){
		String campos = "";
//		cod[j] = "0";
		int cdEventoExt = 0;
		Log.i(LOG, "ARRAY[8]='"+array[8]+"'");
		if(array[8].equals("''")){
			cdEventoExt = (pegaUltimoCdExt()+1);
			Log.i(LOG, "!!!!!cdeventoExt:"+cdEventoExt);
			array[8] = cdEventoExt+"";
//			cod[j] = cdEventoExt+"";
		}
		Log.i(LOG, "cdEventoExt=:"+cdEventoExt);
		campos += "null,"+array[8]+","+array[1]+","+array[2]+","+array[9]+","+array[3]+","+array[4]+","+array[5]+","+array[6]+","+array[7];
			
		conectAgenda.insert(campos);
		Log.i(LOG, campos+" |inserido na AGENDA");

		if(cdEventoExt != 0){
			conectAgenda.setClausula(" WHERE CDEVENTOEXT='"+array[8]+"'");
			String dt = MyString.tString(conectAgenda.select("DATA"));
			dt = dt.replace( "\\" , ""); 
			String aux2 = MyString.tString(conectAgenda.select("HORAINICIO"));
			String hI = aux2.substring(0, 2) +":";
			hI += aux2.substring(2, 4);
			try {
				updateCodServidor(dt, hI, cdU, array[8]);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
			
		return campos;
	} 
}