package btm.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class DataJp extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ClienteIso.Pref =  getSharedPreferences("com.app", Context.MODE_PRIVATE);
		ClienteIso.wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		ClienteIso.contextTW = this.getApplicationContext();
		
		//System.out.println("INICIANDO CLASE EXTENDIDA "+getLocalClassName());
	}
	
	public void onVolver(View view){
		finish();
	}
	
	public void jp(int mti, String pc, String cp60, String cp61, String cp62, String cp63, int val){
		new DataISO().execute(mti, pc, cp60, cp61, cp62, cp63, val);
	}
	
	//NUEVA FUNCION SIN SSL
	public void jp_h(int mti, String pc, String cp60, String cp61, String cp62, String cp63, int val, int pto){
		new DataISO().execute(mti, pc, cp60, cp61, cp62, cp63, val, pto);
	}

	public void alert_info(String res, String title, int icon){
		Builder alert = new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(res)
        .setIcon(icon)
        .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	finish();
            }
         })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            	finish();
            }
         });

		alert.show();
	}

	public void alert_info(String res, String title, int icon,
						   DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancelar, String positivo, String negativo){

		Builder alert = new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(res)
        .setIcon(icon)
        .setPositiveButton(android.R.string.ok, ok)
        .setNegativeButton(android.R.string.cancel, cancelar)
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
         });
		if(!positivo.isEmpty())alert.setPositiveButton(positivo, ok);
		if(!negativo.isEmpty())alert.setNegativeButton(negativo, cancelar);
		alert.show();
	}

	public void alert_info(int res, int title, int icon,
						   DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancelar, String positivo, String negativo){
		Builder alert = new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(res)
        .setIcon(icon)
        .setPositiveButton(android.R.string.ok, ok)
        .setNegativeButton(android.R.string.cancel, cancelar)
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
         });
		if(!positivo.isEmpty())alert.setPositiveButton(positivo, ok);
		if(!negativo.isEmpty())alert.setNegativeButton(negativo, cancelar);
		alert.show();
	}
	
	/*CLIENTE ISO*/
	private class isoResult{
	   	 String[] result = new String[7];
	   	 String code = "";
	   	 public Exception e;
	    }
	
	private class DataISO extends AsyncTask<Object, Void, isoResult> {
		
		ProgressDialog pDialog = new ProgressDialog(DataJp.this);
    	Integer c39 = -1;
    	
    	protected void onPreExecute (){
       		
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getResources().getString(R.string.inf_dialog));
            pDialog.setCancelable(false);
            
            pDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                	DataISO.this.cancel(false);
                }
            });
     
                pDialog.show();
       		
       	 }

		@Override
		protected isoResult doInBackground(Object... param) {
			isoResult task = new isoResult();
			try {
				task.code = (String)param[1];
				if(param.length == 7){
				task.result=ClienteIso.msjIso((Integer)param[0], (String)param[1], (String)param[2], (String)param[3],
						(String)param[4], (String)param[5], (Integer)param[6]);
					Log.d("parametros", (String) param[1]);
	            	for(String out:task.result){
	            		//System.out.println(out);
	            	}
				} else {
					task.result=ClienteIso.msjIso_h((Integer)param[0], (String)param[1], (String)param[2], (String)param[3],
							(String)param[4], (String)param[5], (Integer)param[6], (Integer)param[7] );
	            	for(String out:task.result){
	            		//System.out.println(out);
	            	}
				}
				
            	
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			task.e = e;
   			task.e.printStackTrace();
   		}
			return task;
		}
		
		@Override
	    protected void onPostExecute(isoResult resp) {
			
			pDialog.dismiss();	
			
			if (resp.e != null){
				 //System.out.println(resp.e);
				 //Toast.makeText(getApplicationContext(), resp.e.toString(), Toast.LENGTH_SHORT).show();
				 //return;
			}

			c39 = (Integer) Integer.parseInt(resp.result[0]);
			if(c39 == 0){
				infoOk(resp.result[1], resp.code);
			} else {
				String aviso;
				try {
					aviso = (resp.result[1].isEmpty())?getResources().getString(R.string.info_fallas_comunicacion)+" - E-01":resp.result[1];
				} catch (Exception e) {
					aviso = getResources().getString(R.string.info_fallas_comunicacion)+" - E-00";
				}

				infoFalla(aviso, resp.code);
			}
		}

	}

	public void infoOk(String string, String pc) {
		// TODO Auto-generated method stub
		
	}
	
	public void infoFalla(String string, String pc){
		
	}



}
