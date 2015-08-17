package Web;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

public class  ExecutaWeb extends Thread{
	private static  String LOG = "teste";
	ResponseHandler<String> handler;
	HttpClient client;
	HttpGet httpGet;
	String url;
	public String respServer;
	
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
			Log.i(LOG, "run:"+respServer);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.i(LOG, "Erro:"+e);
			respServer = "Erro";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(LOG, "Erro:"+e);
			respServer = "Erro";
		}
	}

	public String getRespServer(){
		return respServer;
	}
}
