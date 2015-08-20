package Web;

import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Conexao {
	private static  String LOG = "teste";
	Context context;
	
	public Conexao(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public boolean isConected(){
		Log.i(LOG, "isConected()");
		ConnectivityManager c = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(c != null
				&&((c.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
				(c.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))
				){
			return true;
		}
		return false;
	}
	
	public boolean isWifi(){
		ConnectivityManager c = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
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
			Log.i("pingOut", out);
			if(out.contains("100%")){
				return false;
			}
			
		}
		catch(Exception e){
			Log.i(LOG,"error"+e);
//			onDestroy();
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
				else if(!isLocal(url)){
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
