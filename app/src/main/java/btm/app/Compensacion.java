package btm.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;

import btm.app.DataHolder.DataHolder;

public class Compensacion extends DataJp {
    public static final String TAG = "DEV -> Compensación ";

    AlertDialog dialog_pass_ui;

    private Button clic;
    private EditText monto;
    private Spinner tipo;
    private TextView tvcuenta;
    private CuentaB cuenta;
    private int val;

    String password_dialog;
    String data, username, password;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compensacion);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.compensacion));

        SharedPreferences shPref = getSharedPreferences(getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = shPref.getString(LoginActivity.USERNAME,"--");
        final String userp = shPref.getString(LoginActivity.PASSPIN,"0000");

        clic         = (Button) findViewById(R.id.buttonCompensacion);
        monto        = (EditText) findViewById(R.id.editTextMonto);
        tipo         = (Spinner) findViewById(R.id.spinnerTipoCompensacion);
        tvcuenta     = (TextView) findViewById(R.id.textViewCuentaCompensacion);

        username  = DataHolder.getUsername();
        password  = DataHolder.getPass();

        final Request request = new Request(this);
        String datos = "&username=" + user+"&token="+request.tk();

        request.http_get("datoscuenta", datos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if (response.isEmpty() || !response.contains("|")) {
                        tvcuenta.setText(R.string.alerta_cuentas);
                        clic.setEnabled(false);
                        return;
                    }
                    cuenta = new CuentaB(response);
                    tvcuenta.setText(cuenta.toString());
                } catch (Exception e) {
                    tvcuenta.setText(R.string.alerta_cuentas);
                }
            }
        });

        clic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                val = convertMontoValue(monto.getText().toString());

                if(monto.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.ingrese_valor, Toast.LENGTH_LONG).show();
                    return;
                } else if(val==0){
                    Toast.makeText(getApplicationContext(), R.string.error_monto, Toast.LENGTH_LONG).show();
                    return;

                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Add the buttons
                    builder.setMessage(R.string.ui_buy_compensation_dialog_message);
                    builder.setPositiveButton(R.string.ui_buy_token_ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            passwordDialog();
                        }
                    });
                    builder.setNegativeButton(R.string.ui_buy_token_cancel_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    private class AsyncGetHttpData extends AsyncTask<String, Void, String> {
        ProgressDialog progress = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            progress.setMessage(getString(R.string.inf_dialog));
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    //datos:username|password|monto|tipo|numero|banco|
                    String info_compensa = username+"|"+password_dialog+"|"+monto.getText().toString()+"|"+
                                           "Transferencia" +"|"+
                                           cuenta.getNumero()+"|"+cuenta.getBanco()+"|";

                    try {
                        data = new btm.app.Network.NetActions(context).requestCompensation(info_compensa);
                        Log.d(TAG, " oKHttp response: " + data);

                        customDialog(data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void passwordDialog(){
        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = Compensacion.this.getLayoutInflater();

        //final EditText input = (EditText) findViewById(R.id.password);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.ui_aux_pass, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.password);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder_pass_dialog.setCancelable(false);
        builder_pass_dialog.setView(viewInflated)
                .setPositiveButton(R.string.ui_general_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        password_dialog = input.getText().toString();
                        Log.d(TAG, " -> " + password_dialog);
                        new AsyncGetHttpData().execute("");
                        dialog_pass_ui.dismiss();
                    }
                })
                .setNegativeButton(R.string.ui_general_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog_pass_ui.dismiss();
                    }
                });

        dialog_pass_ui = builder_pass_dialog.create();
        dialog_pass_ui.show();
    }

    public int convertMontoValue(String valur_str){
        if(valur_str.isEmpty()){
            return 0;
        }else{
            return Integer.valueOf(valur_str);
        }
    }

    public void onBackPressed(){
        Intent gotoHome = new Intent(context, MainActivity.class);
        startActivity(gotoHome);
    }

    public void customDialog(String okhttpResponse){
        AlertDialog.Builder builder = new AlertDialog.Builder(Compensacion.this);

        builder.setMessage(okhttpResponse);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
