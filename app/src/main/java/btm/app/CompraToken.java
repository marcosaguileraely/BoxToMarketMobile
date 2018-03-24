package btm.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.util.ArrayList;

import btm.app.DataHolder.DataHolder;
import btm.app.Network.NetActions;

import static btm.app.R.id.spinner;

public class CompraToken extends DataJp{
    public static final String TAG = "DEV -> Buy token ";

    Context context = this;
    AlertDialog dialog_pass_ui;
    ProgressDialog progress;

    private Spinner tC, metodo;
    private EditText email, monto;
    private Button clic, addCreditCard;
    private ArrayList<CC> listTc;
    private String username, password, modo, email_user, datafull;

    private int val;

    String password_dialog;
    String data;
    String card_info;
    String card_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_token);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.buy_a_token_title));

        tC            = (Spinner) findViewById(R.id.spinnerTipoTC);
        metodo        = (Spinner) findViewById(R.id.spinnerTipoCompra);
        email         = (EditText) findViewById(R.id.editTextEmail);
        monto         = (EditText) findViewById(R.id.editTextMonto);
        addCreditCard = (Button) findViewById(R.id.addCreditCardButton);
        clic          = (Button) findViewById(R.id.buttonCompraToken);

        progress = new ProgressDialog(context);
        progress.setMessage(getString(R.string.inf_dialog));
        progress.setCancelable(false);

        username  = DataHolder.getUsername();
        //password  = DataHolder.getPass();

        tC.setVisibility(View.GONE);
        addCreditCard.setVisibility(View.GONE);

        listTc = new ArrayList<CC>();

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
                final ProgressDialog progress = new ProgressDialog(context);
                progress.setMessage(getString(R.string.inf_dialog));

                modo = parent.getItemAtPosition(position).toString();
                String datos = username;

                Log.d("Comprar token ", "--> "+ modo);

                if(modo.equals("Tarjeta de Credito") || modo.equals("Credit Card")){
                    tC.setVisibility(View.VISIBLE);
                    try {
                        data = new NetActions(context).getCardList(datos);
                        Log.d(TAG, " oKHttp response: " + data);

                        if(data.contains("****")){
                            String[] inf = data.replace("|", ";").split(";");
                            for (String cc : inf) {
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            tC.setAdapter(adapter);
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.ui_buy_token_no_credit_cards), Toast.LENGTH_LONG).show();
                            addCreditCard.setVisibility(View.VISIBLE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    addCreditCard.setVisibility(View.GONE);
                    listTc = new ArrayList<CC>();
                    tC.setVisibility(View.GONE);
                    tC.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val        = convertMontoValue(monto.getText().toString());
                email_user = email.getText().toString();

                if(email.getText().toString().isEmpty() || monto.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.ui_buy_token_empty_fields, Toast.LENGTH_LONG).show();
                    return;

                } else if(val == 0){
                    Toast.makeText(getApplicationContext(), R.string.error_monto, Toast.LENGTH_LONG).show();
                    return;

                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Add the buttons
                    builder.setMessage(R.string.ui_buy_token_dialog_message);
                    builder.setCancelable(false);
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

        tC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                card_info = parent.getItemAtPosition(position).toString();
                card_id   = listTc.get(position).getId();
                Log.d(TAG, "card number: "+card_info + "-->"+ card_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CardActivity.class);
                startActivity(intent);
            }
        });
    }

    private class AsyncGetHttpData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String paymentMethod = paymentMethod(modo);
                    String card_number_id = card_id;

                    datafull = getDataConcat(username, password_dialog, paymentMethod, val, card_number_id, email_user);

                    // datos = "h0m3data|g0ldfish1|username|clave|metodopago|monto|idTarjetas|email||"
                    Log.d(TAG, " --> " + password_dialog + " Datos : " + datafull);

                    try {
                        data = new btm.app.Network.NetActions(context).buyToken(datafull);
                        Log.d(TAG, " oKHttp response: " + data);

                        if(data.contains("El Token generado es")){
                            progress.dismiss();
                            customDialog();

                        }else{
                            //String warning_msg = getString(R.string.ui_buy_token_message_error);
                            pushToast(CompraToken.this, data);
                        }

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
        final LayoutInflater inflater = CompraToken.this.getLayoutInflater();

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
                        if(password_dialog.equals(DataHolder.getPass())){
                            dialog_pass_ui.dismiss();
                            new AsyncGetHttpData().execute("");
                        }else{
                            dialog_pass_ui.dismiss();
                            customPasswordDialog("Clave invalida. Intenta nuevamente.");
                        }
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

    public void customDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(data);
        builder.setCancelable(false);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void pushToast(Activity view, final String message){
        view.runOnUiThread(new Runnable() {
            public void run(){
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getDataConcat(String user, String pass, String buy_method, int pay_value, String idcard, String mail){
        if(buy_method.equals("tarjeta")){
            return "h0m3data|g0ldfish1|" + user +"|"+ pass +"|"+ buy_method + "|" + pay_value +"|"+ idcard +"|"+ mail +"||";
        } else{
            return "h0m3data|g0ldfish1|" + user +"|"+ pass +"|"+ buy_method + "|" + pay_value +"||"+ mail +"||";
        }
    }

    public String paymentMethod(String method){
        if(method.equals("Tarjeta de Crédito") || method.equals("Credit Card")){
            return "tarjeta";
        }
        else{
            return "creditos";
        }
    }

    public int convertMontoValue(String valur_str){
        if(valur_str.isEmpty()){
            return 0;
        }else{
            return Integer.valueOf(valur_str);
        }
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

    @Override
    public void infoOk(String string, String pc) {
        super.infoOk(string, pc);
        //Log.d("DATAJPOS", string);
        alert_info(string, "Confirmacion", android.R.drawable.ic_dialog_info);
    }

    @Override
    public void infoFalla(String string, String pc) {
        super.infoFalla(string, pc);
        //Log.e("DATAJPOS", string);
        alert_info(string, "Info", android.R.drawable.ic_dialog_alert);
    }

    public void customPasswordDialog(String inDatum){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(inDatum);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                //mBluetoothLeService.disconnect();
                //Intent intent = new Intent(context, MainActivity.class);
                //startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
