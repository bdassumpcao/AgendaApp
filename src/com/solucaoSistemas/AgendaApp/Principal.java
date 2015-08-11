package com.solucaoSistemas.AgendaApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Utilitarios.MyString;
import Web.Conexao;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.GetChars;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Bruno Lopes.
 */
public class Principal extends Activity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {
 
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_DAY_VIEW;
    private WeekView mWeekView;
    private ConectaLocal conectAgenda, conectUser, conectConfig, conectLogAgenda;
    private String[]  campoCdEvento, campoDescricao, campoData, campoHoraInicio, campoHoraFim, campoStatus;
    private String cdusuario, usuario, uDescricao, dataSelecionada;
    private TextView tvUsuario, tvUdescricao;
    private AlertDialog alerta;
    private int horaExpediente = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        
		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().hide();
        
        
		//conexões com as tabelas do banco
        conectAgenda = new ConectaLocal(getApplicationContext(), "AGENDA");
        conectConfig = new ConectaLocal(getApplicationContext(), "CONFIGURACOES");
        conectUser = new ConectaLocal(getApplicationContext(), "USUARIO"); 
        conectLogAgenda = new ConectaLocal(getApplicationContext(), "LOGAGENDA");
        
        //inicia serviço de sincronização
        if(MyString.tString(conectConfig.select("SINCRONIZAR")).equals("1")){
        	boolean alarmeAtivo = (PendingIntent.getBroadcast(this, 0, new Intent("SINCRONIZACAO_AGENDA"), PendingIntent.FLAG_NO_CREATE) == null);
			
			if(alarmeAtivo){
				Log.i("teste", "Alarme Agenda");
				
				Intent intent = new Intent("SINCRONIZACAO_AGENDA");
				PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);
				
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				c.add(Calendar.SECOND, 3);
				
				AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 120000, p);
			}
			
        	boolean alarmeAtivo2 = (PendingIntent.getBroadcast(this, 0, new Intent("SINCRONIZACAO_TAREFA"), PendingIntent.FLAG_NO_CREATE) == null);
			
			if(alarmeAtivo2){
				Log.i("teste", "Alarme Tarefa");
				
				Intent intent = new Intent("SINCRONIZACAO_TAREFA");
				PendingIntent p = PendingIntent.getBroadcast(this, 0, intent, 0);
				
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				c.add(Calendar.SECOND, 3);
				
				AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarme.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 300000, p);
			}
	    }        
        
 
        conectUser.setClausula(" WHERE STATUS=1");	
    	cdusuario = MyString.tString(conectUser.select("CDUSUARIO"));  
    	usuario = MyString.tString(conectUser.select("NMUSUARIO"));
    	uDescricao = MyString.tString(conectUser.select("DSCARGO")); 
    	tvUsuario = (TextView)findViewById(R.id.tvUsuario);
    	tvUsuario.setText(usuario);
    	tvUdescricao = (TextView)findViewById(R.id.tvUdescricao);
    	tvUdescricao.setText(uDescricao);
   
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);
        
        Bundle b;
        if(getIntent().getExtras() != null){
        	b = getIntent().getExtras();
        	dataSelecionada = b.getString("key");
        	dataSelecionada = dataSelecionada.replace( "/" , "");   
        	
        	Calendar date = Calendar.getInstance(); 
            int d = Integer.parseInt(dataSelecionada.substring(0,2));
            int m = Integer.parseInt(dataSelecionada.substring(2,4));
            int a = Integer.parseInt(dataSelecionada.substring(4,8));
            
            date.set(Calendar.DAY_OF_MONTH, d);
          	date.set(Calendar.MONTH, m-1);
          	date.set(Calendar.YEAR, a);          	
          	mWeekView.goToDate(date);
          	mWeekView.goToHour(horaExpediente);
        }
        else{
        	mWeekView.goToToday();
        	mWeekView.goToHour(horaExpediente);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
    		case R.id.action_tarefas:
	    		Intent intent3 = new Intent(Principal.this, Tarefas.class);
	    		Principal.this.startActivity(intent3);
	    		Principal.this.finish();
	    		return true;	
	    	case R.id.action_configuracoes:
	    		Intent intent2 = new Intent(Principal.this, Configuracoes.class);
	    		Principal.this.startActivity(intent2);
	    		Principal.this.finish();
	    		return true;	   
	    	case R.id.action_atualizar:
	            if(MyString.tString(conectConfig.select("SINCRONIZAR")).equals("0")){		            		 
	            	Conexao conexao = new Conexao(this);
	            	if(!conexao.isConected())
	            		showToast("Sem Conexão");
	            	else{  
	            		Intent intent = new Intent(Principal.this, Splash.class);
	            		Intent intentt2 = new Intent(Principal.this, Splash2.class);
	                	//startActivity(intent);
	                	startActivity(intentt2);
	            	}

	                mWeekView.goToToday(); 
	                mWeekView.goToHour(horaExpediente);
	    	    }  
	            else
	            	showToast("Sincronização automática já está ativada!");
	    		return true;
        	case R.id.action_novoEvento:
        		Calendar date;
                date = mWeekView.getLastVisibleDay();
                int d = date.get(Calendar.DAY_OF_MONTH);
                int m = date.get(Calendar.MONTH)+1;
                int a = date.get(Calendar.YEAR);
                String data = "";
                if(d<10)
                	data = "0"+d;
                else if(d>=10)
                	data = d+"";
                if(m<10)
                	data += "0"+m;
                else if(m>=10)
                	data += m;
                data += a;              	

        		Intent intent = new Intent(Principal.this, InsereEvento.class); 
   				Bundle b = new Bundle();
   				b.putString("key", data);
   				intent.putExtras(b);
        	    Principal.this.startActivity(intent);
        	    Principal.this.finish();
        	    return true;        	    
            case R.id.action_today:
                mWeekView.goToToday(); 
                mWeekView.goToHour(horaExpediente);
                return true;
            case R.id.action_search_day:
            	final Calendar c = Calendar.getInstance();
            	final int mAno, mMes, mDia;            	
                mAno = c.get(Calendar.YEAR);
                mMes = c.get(Calendar.MONTH);
                mDia = c.get(Calendar.DAY_OF_MONTH);
                
                DatePickerDialog dpd = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {     
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth) {   
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth); 
                                mWeekView.goToDate(c);
                                mWeekView.goToHour(horaExpediente);
                            }
                        }, mAno, mMes, mDia);   
                dpd.show();                          
            	return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);
                    mWeekView.goToHour(horaExpediente);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);
                    mWeekView.goToHour(horaExpediente);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);
                    mWeekView.goToHour(horaExpediente);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        WeekViewEvent event;
        
    	conectAgenda.setClausula("WHERE CDUSUARIO='"+cdusuario+"'");

    	if(MyString.tString(conectConfig.select("BAIXADO")).equals("0")){
    		conectAgenda.setClausula("WHERE CDUSUARIO='"+cdusuario+"' AND STATUS=0");
    	}
    	
    	campoDescricao = MyString.tStringArray(conectAgenda.select("DESCRICAO").toString());
    	campoData = MyString.tStringArray(conectAgenda.select("DATA").toString());
    	campoHoraInicio = MyString.tStringArray(conectAgenda.select("HORAINICIO").toString());
    	campoHoraFim = MyString.tStringArray(conectAgenda.select("HORAFIM").toString());
    	campoCdEvento = MyString.tStringArray(conectAgenda.select("CDEVENTO").toString());
    	campoStatus = MyString.tStringArray(conectAgenda.select("STATUS").toString());	
    	
    	
    	for(int i=0; i < campoDescricao.length ; i++){
    		campoStatus[i] = MyString.tiraEspaço(campoStatus[i]);
    		campoData[i] = MyString.tiraEspaço(campoData[i].replace("\\", "")); 
    		int month = Integer.parseInt(MyString.tiraEspaço(campoData[i].substring(3, 5)));
    		if(month == newMonth){
    			int cdEvento = Integer.parseInt(MyString.tiraEspaço(campoCdEvento[i]));
            	int day_of_month = Integer.parseInt(MyString.tiraEspaço(campoData[i].substring(0, 2)));        	
            	int year = Integer.parseInt(MyString.tiraEspaço(campoData[i].substring(6, 10)));      	
            	campoHoraInicio[i] = MyString.tiraEspaço(campoHoraInicio[i]);
            	int start_hour_of_day = Integer.parseInt(campoHoraInicio[i].substring(0, 2));
            	int start_minute = Integer.parseInt(campoHoraInicio[i].substring(2, 4));
            	campoHoraFim[i] = MyString.tiraEspaço(campoHoraFim[i]);
            	int end_hour_of_day = Integer.parseInt(campoHoraFim[i].substring(0, 2));
            	int end_minute = Integer.parseInt(campoHoraFim[i].substring(2, 4));
            	String title = campoDescricao[i];
          
            	
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.DAY_OF_MONTH, day_of_month);
                startTime.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
                startTime.set(Calendar.MINUTE, start_minute);
                startTime.set(Calendar.MONTH, month-1);
                startTime.set(Calendar.YEAR, year);
                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.DAY_OF_MONTH, day_of_month);
                endTime.set(Calendar.HOUR_OF_DAY, end_hour_of_day);
                endTime.set(Calendar.MINUTE, end_minute);
                endTime.set(Calendar.MONTH, month-1);
                endTime.set(Calendar.YEAR, year);
                event = new WeekViewEvent(cdEvento, getEventTitle(startTime, title), startTime, endTime);
                if(campoStatus[i].equals("1")){
                	event.setColor(getResources().getColor(R.color.event_color_02));
                }
                else{
                	event.setColor(getResources().getColor(R.color.event_color_01));
                }
                events.add(event);
    		}
    		
    	}
        

        return events;
    }

    private String getEventTitle(Calendar time, String title) {
        return String.format(title+"  %02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(Principal.this, "Clicked " + event.getId(), Toast.LENGTH_SHORT).show();        
    	String cdevento = MyString.tString(event.getId()); 
        Calendar date = Calendar.getInstance();
        date = event.getStartTime();
        int d = date.get(Calendar.DAY_OF_MONTH);
        int m = date.get(Calendar.MONTH)+1;
        int a = date.get(Calendar.YEAR);
        String data = "";
        if(d<10)
        	data = "0"+d;
        else if(d>=10)
        	data = d+"";
        if(m<10)
        	data += "0"+m;
        else if(m>=10)
        	data += m;
        data += a;        
        
        String[] dados = {cdevento,data};        
        
        Intent intent = new Intent(this, EditaEvento.class);
        Bundle b = new Bundle();
        b.putStringArray("key", dados);
        intent.putExtras(b);
        Principal.this.startActivity(intent);
        Principal.this.finish(); 
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
    	conectAgenda.setClausula("WHERE CDEVENTO = "+event.getId()+"");
    	String status = MyString.tString(conectAgenda.select("STATUS"));
    	if(status.equals("1"))
    		showToast("Evento já está baixado!");
   		else{
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Confirma exclusão do evento "+event.getName()+"?");
	        builder2.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface arg0, int arg1) {
	       			deletar(event.getId(), event.getStartTime());	       			
	        	}
	        });
	        builder2.setNegativeButton("Não", new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface arg0, int arg1) {
	        		
	        	}
	        });
	        alerta = builder2.create();
	        alerta.show();
   		}
    }
    
    
    public void deletar(long eventID, Calendar date){
   		conectAgenda.setClausula(" WHERE CDEVENTO = "+eventID+"");
   		String cdExt = MyString.tString(conectAgenda.select(" CDEVENTOEXT "));
   		if(conectAgenda.delete()){
   			cdExt = MyString.normalize(cdExt);
   			conectLogAgenda.insert(eventID+","+cdExt+",'D'"); // 'D' = delete
   			mWeekView.goToDate(date);
   			mWeekView.goToHour(horaExpediente);
   		}
    }
    
    
	 public  void startService(){
			Intent intent = new Intent("SERVICO_AGENDA");
			startService(intent);
			
//			startService(new Intent("SERVICO_TAREFA"));
	 }

	  
	 public void stopService(){
			Intent intent = new Intent("SERVICO_AGENDA");
			stopService(intent);
			
			stopService(new Intent("SERVICO_TAREFA"));
	 }
    
    public void showToast(String texto){		
		Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
	}
      
}
