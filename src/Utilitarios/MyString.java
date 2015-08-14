package Utilitarios;

import android.util.Log;

public  class MyString {
	private static  String LOG = "teste";
	static String[] cod;
	/** Para a normalização dos caracteres de 32 a 255, primeiro caracter */  
	public static final char[] FIRST_CHAR =  
		    (" !'#$%&'()*+\\-./0123456789:;<->?@ABCDEFGHIJKLMNOPQRSTUVWXYZ"  
		        + "[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ E ,f'.++^%S<O Z  ''''.-"  
		        + "-~Ts>o ZY !C#$Y|$'(a<--(_o+23'u .,1o>113?AAAAAAACEEEEIIIIDNOO"  
		        + "OOOXOUUUUyTsaaaaaaaceeeeiiiidnooooo/ouuuuyty")  
		        .toCharArray();  
	/** Para a normalização dos caracteres de 32 a 255, segundo caracter */  
	public static final char[] SECOND_CHAR =  
		    ("  '         ,                                               "  
		        + "\\                                   $  r'. + o  E      ''  "  
		        + "  M  e     #  =  'C.<  R .-..     ..>424     E E            "  
		        + "   E E     hs    e e         h     e e     h ")  
		        .toCharArray(); 
	
	
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
	
	
	public static String retiraQuebraLinha(String string){
		if(!string.equals("")){
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
		    }
		    
		    else if(aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' ||aux[i] == ','){
		     
		    }
		    
		    else if( aux[i] == '§'){
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
	
	/**Retorna a ultima posição de uma array de strings que vem em formato Object
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
	
	/**retira os espaços das primeiras posiçoes, ideal para as consultas do banco*/ 
	public static String tiraEspaço(String string){
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
	
	/**retira todos os caracteres que a consulta sqlite retorna, e retorna em uma string só*/
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
	
	/**retira todos os caracteres que a consulta sqlite retorna, exceto virgulas, e retorna em uma string só*/
	public static String tString3(Object string){
		
		String resul = string.toString();
		char[] aux = new char[resul.length()];
		
		for(int i = 0; i < resul.length(); i++){
			aux[i] = resul.charAt(i);
		}
		
		resul = "";
		
		for(int i = 0; i < aux.length; i++){
			
			if(aux[i] == '$' || aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ':'){
				
			}
			else{
				resul += aux[i];
			}
		}
		
		
		return resul;
	}
	
	/**retira todos os caracteres que a consulta sqlite retorna, incluindo \, e retorna em uma string só*/
	public static String tString4(Object string){
		
		String resul = string.toString();
		char[] aux = new char[resul.length()];
		
		for(int i = 0; i < resul.length(); i++){
			aux[i] = resul.charAt(i);
		}
		
		resul = "";
		
		for(int i = 0; i < aux.length; i++){
			
			if(aux[i] == '\\' || aux[i] == '$' || aux[i] == ']' || aux[i] == '[' || aux[i] == '}' || aux[i] == '{' || aux[i] == '"' || aux[i] == ',' || aux[i] == ':'){
				
			}
			else{
				resul += aux[i];
			}
		}
		
		
		return resul;
	}
	
	public static String[] montaInsertUsuario(String resultGet){
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
		String[] r = new String[3];
		
		
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
				re[j] = ordenaUser(r, j);
				j++;
			}
			else{
				nm += aux[i];
			}
		}
		
	return re;
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
				re[j] = ordena(r, j);
				j++;
			}
			else{
				nm += aux[i];
			}
		}
		
		return re;
	}
	
	public static String[] montaInsertTarefa(String resultGet){
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
		String [] r = new String[8];
		
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
				re[j] = ordenaTarefa(r, j);
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
		retorno += "null,"+array[0]+","+array[1]+","+array[2]+","+array[9]+","+array[3]+","+array[4]+","+array[5]+","+array[6]+","+array[7];
		return retorno;
	}
	
	private static String ordenaUser(String[] array, int j){
		String retorno = "";
		retorno += array[0]+","+array[1]+","+array[2]+",0";
		return retorno;
	}
	
	private static String ordenaTarefa(String[] array, int j){
		String retorno = "";
		cod[j] = array[0];
		retorno += "null,"+array[1]+","+array[2]+","+array[3]+","+array[4]+","+array[5]+","+array[6].replace("-", "/")+","+array[7].replace("-", "/");
//		Log.i(LOG, "retorno:"+retorno);
		return retorno;
	}
	
	public static void setCod(String [] c){
		cod = c;
	}
	
	public static String[] getCod(){
		return cod;
	}

}
