package btm.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderMachineSearch;
import btm.app.DataHolder.DataHolderSubs;
import btm.app.Network.NetActions;
import btm.app.Utils.Utils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SearchMachinePayActivity extends AppCompatActivity {

    public static final String TAG = "DEV -> Pagar ";
    public Context context = this;
    AlertDialog dialog_pass_ui;
    ProgressDialog progress;

    public Button pay;
    public Spinner payment_method, credit_card;
    private ImageView detail_img;
    private TextView title, total_ammount;
    public EditText token;

    public ArrayList<CC> listTc;

    public String modo, datafull, username, data, datos;
    public String card_info;
    public String card_id;
    String finalmodel = "";
    String password_dialog;
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_machine_pay);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.pay_search_machine_title));

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        progress.setCancelable(false);

        final NetActions netActions =  new NetActions(context);

        payment_method = (Spinner) findViewById(R.id.spinnerTipoRecarga);
        credit_card    = (Spinner) findViewById(R.id.spinnerCreditCards);
        token          = (EditText) findViewById(R.id.token);

        title          = (TextView) findViewById(R.id.pay_detail_title);
        total_ammount  = (TextView) findViewById(R.id.pay_detail_total_ammount);
        pay            = (Button) findViewById(R.id.pay_button);

        username       = DataHolder.getUsername();

        token.setVisibility(GONE);
        listTc = new ArrayList<CC>();

        total_ammount.setText("$" + DataHolderMachineSearch.getTotal_pay());

        payment_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modo = parent.getItemAtPosition(position).toString();

                Log.d(TAG, "----->" + modo);

                if( modo.equals("Tarjeta de Crédito") || modo.equals("Credit Card")){
                    Log.d(TAG, "metodo tarjeta");
                    credit_card.setVisibility(VISIBLE);
                    token.setVisibility(GONE);

                    String datos = "&username="+ DataHolder.getUsername();
                    Log.d(TAG, "datos: "+datos);

                    netActions.http_get_data("menutarjetas", datos, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("Token")) {
                                Toast.makeText(getApplicationContext(), response + getString(R.string.error_token), Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            if (response.contains("No tiene")) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                return;
                            }

                            String[] inf = response.split("\\|");
                            for (String cc : inf) {
                                Log.d(TAG, "cc: "+cc);
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            credit_card.setAdapter(adapter);
                        }
                    });

                }
                if( modo.equals("Mi Billetera") || modo.equals("My Wallet")){
                    token.setVisibility(GONE);
                    credit_card.setVisibility(GONE);

                }
                if( modo.equals("token") || modo.equals("Token")){
                    token.setVisibility(VISIBLE);
                    credit_card.setVisibility(GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        credit_card.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(convertPaymentMode(modo).equals("Tarjeta")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Add the buttons
                    builder.setMessage(R.string.pay_search_machine_confirm_msg);
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

                }else if(convertPaymentMode(modo).equals("Creditos")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Add the buttons
                    builder.setMessage(R.string.pay_search_machine_confirm_msg);
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
                    // datos = username|clave|serial_buscado|metodo_de_pago|idTarjetas|monto|productos|
                    // productos = posicion1,cantidad1-posicion2,cantidad2-posicion3,cantidad3-... //LISTA DE BOXES CON LA CANTIDAD SOLICITADA
                    // idTarjetas = es obligatorio sólo para el método de pago Tarjeta de Crédito

                    datafull = getDataConcat(DataHolder.getUsername(),
                                             password_dialog,
                                             DataHolderMachineSearch.getMachine_code(),
                                             convertPaymentMode(modo),
                                             card_id,
                                             DataHolderMachineSearch.total_pay,
                                             DataHolderMachineSearch.getLines_to_pay());

                    Log.d(TAG, " --> " + password_dialog + " Datos : " + datafull);

                    data = utils.payMachineProducts(datafull);
                    Log.d(TAG, " oKHttp response: " + data);
                    progress.dismiss();
                    if( data.contains(".png") ){
                        DataHolderMachineSearch.setQr_code(data);
                        Intent goToResumeQr = new Intent(context, SearchedMachinePaidQrActivity.class);
                        startActivity(goToResumeQr);
                    }else {
                        customDialog(data);
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
        final LayoutInflater inflater = SearchMachinePayActivity.this.getLayoutInflater();

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

    public String getDataConcat(String user, String pass, String serial, String payment_method, String idcard, int total_pay, String line_to_pay ){
        if(payment_method.equals("Tarjeta")){
            return user +"|"+ pass +"|"+ serial + "|" + payment_method + "|" + idcard +"|" + total_pay + "|"+ line_to_pay +"|";

        } else {
            return user +"|"+ pass +"|"+ serial + "|" + payment_method + "|" + "|" + total_pay + "|" + line_to_pay + "|";
        }
    }

    public void customDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
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

    public String convertPaymentMode(String mode){
        if(mode.equals("Tarjeta de Crédito") || mode.equals("Credit Card")){
            return finalmodel = "Tarjeta";

        }
        if(mode.contains("Mi Billetera") || mode.contains("My Wallet")){
            return finalmodel = "Creditos";

        }
        if(mode.equals("token") || mode.equals("Token")){
            return finalmodel = "Token";
        }

        return finalmodel;
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
