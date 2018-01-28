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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.util.ArrayList;

import btm.app.DataHolder.DataHolder;
import btm.app.Network.NetActions;

public class ChargeActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Add money";
    private Context context = this;

    AlertDialog dialog_pass_ui;

    private Button addMoney, addCreditCard;
    private EditText value, token;
    private Spinner addMoneyMethod, creditCardList;
    private ArrayList<CC> listTc;
    private TextView user_text;
    private String username_aux, modo, username, data, datafull;

    String password_dialog;
    String card_info, card_id;
    String token_val;

    int val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_recarga));
        final NetActions netActions = new NetActions(context);

        creditCardList    = (Spinner) findViewById(R.id.spinnerCreditCards);
        addMoneyMethod    = (Spinner) findViewById(R.id.spinnerTipoRecarga);
        token             = (EditText) findViewById(R.id.token);
        value             = (EditText) findViewById(R.id.value);
        addMoney          = (Button) findViewById(R.id.ChargeButton);
        addCreditCard     = (Button) findViewById(R.id.addCreditCardButton);

        username  = DataHolder.getUsername();

        listTc = new ArrayList<CC>();
        final DataJp dataJp = new DataJp();

        SharedPreferences shPref = getSharedPreferences(getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = shPref.getString(LoginActivity.USERNAME,"--");
        final String userp = shPref.getString(LoginActivity.PASSPIN,"0000");

        username_aux = user;

        addMoneyMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modo = parent.getItemAtPosition(position).toString();
                Log.d(TAG, modo);

                if(modo.equals("Tarjeta de Crédito") || modo.equals("Credit Card")) {
                    token.setVisibility(View.GONE);
                    value.setVisibility(View.VISIBLE);
                    addCreditCard.setVisibility(View.VISIBLE);
                    creditCardList.setVisibility(View.VISIBLE);

                    Request request = new Request(getApplicationContext());
                    String datos = "&username=" + user;

                    try {
                        data = new NetActions(context).getCardList(datos);
                        Log.d(TAG, " oKHttp response: " + data);

                        if (data.contains("****")) {
                            String[] inf = data.replace("|", ";").split(";");
                            for (String cc : inf) {
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            creditCardList.setAdapter(adapter);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.ui_buy_token_no_credit_cards), Toast.LENGTH_LONG).show();
                            addCreditCard.setVisibility(View.VISIBLE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    listTc = new ArrayList<CC>();
                    addCreditCard.setVisibility(View.GONE);
                    creditCardList.setVisibility(View.GONE);
                    value.setVisibility(View.GONE);
                    token.setVisibility(View.VISIBLE);
                    creditCardList.setAdapter(null);
                }




                    /*netActions.http_get_data("menutarjetas", datos, new Response.Listener<String>() {
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

                            String[] inf = response.replace("|", ";").split(";");
                            for (String cc : inf) {
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            // Specify the layout to use when the list of choices appears
                            creditCardList.setAdapter(adapter);

                        }
                    });
                } else {
                    listTc = new ArrayList<CC>();
                    addCreditCard.setVisibility(View.GONE);
                    creditCardList.setVisibility(View.GONE);
                    value.setVisibility(View.GONE);
                    token.setVisibility(View.VISIBLE);
                    creditCardList.setAdapter(null);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        creditCardList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                Intent intent = new Intent(ChargeActivity.this, CardActivity.class);
                startActivity(intent);
            }
        });

        addMoney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                val       = convertMontoValue(value.getText().toString());
                token_val = token.getText().toString();

                if(modo.equals("Tarjeta de Crédito") || modo.equals("Credit Card")){
                    if(value.getText().toString().isEmpty()){
                        Toast.makeText(context, R.string.ingrese_valor, Toast.LENGTH_LONG).show();

                    }else if(val == 0){
                        Toast.makeText(getApplicationContext(), R.string.error_monto, Toast.LENGTH_LONG).show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        // Add the buttons
                        builder.setMessage(R.string.ui_charge_message_dialog);
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
                } else{
                    if(token.getText().toString().isEmpty()){
                        Toast.makeText(context, R.string.ingrese_token, Toast.LENGTH_LONG).show();

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        // Add the buttons
                        builder.setMessage(R.string.ui_charge_message_dialog);
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.ui_buy_token_ok_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AsyncGetHttpData().execute("");
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
            }
        });
    }

    private class AsyncGetHttpData extends AsyncTask<String, Void, String> {
        ProgressDialog dialog2 = new ProgressDialog(ChargeActivity.this);

        @Override
        protected void onPreExecute() {
            dialog2.setMessage(getString(R.string.inf_dialog));
            dialog2.show();
        }

        @Override
        protected String doInBackground(String... params) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String paymentMethod = paymentMethod(modo);
                    String card_number_id = card_id;

                    // datos = "h0m3data|g0ldfish1|username|clave|metodopago|token|monto|idTarjetas|"
                    datafull = getDataConcat(username, password_dialog, paymentMethod, val, card_number_id, token_val);
                    Log.d(TAG, " --> " + password_dialog + " Datos : " + datafull);

                    try {
                        data = new btm.app.Network.NetActions(context).chargeMoneyToMyWallet(datafull);
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
            dialog2.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public int convertMontoValue(String valur_str){
        if(valur_str.isEmpty()){
            return 0;
        }else{
            return Integer.valueOf(valur_str);
        }
    }

    public String getDataConcat(String user, String pass, String buy_method, int pay_value, String idcard, String token){
        // datos = "h0m3data|g0ldfish1|username|clave|metodopago|token|monto|idTarjetas|"
        if(buy_method.equals("tarjeta")){
            //if the payment method is tarjeta, the token space goes empty
            return "h0m3data|g0ldfish1|" + user +"|"+ pass +"|"+ buy_method + "||" + pay_value + "|" + idcard +"|";
        } else{
            //if the payment method is token, the idcard space goes empty
            return "h0m3data|g0ldfish1|" + user +"|"+ DataHolder.getPass() +"|"+ buy_method + "|" + token + "|" + "||";
        }
    }

    public String paymentMethod(String method){
        if(method.equals("Tarjeta de Crédito") || method.equals("Credit Card")){
            return "tarjeta";
        }
        else{
            return "token";
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

    public void passwordDialog(){
        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = ChargeActivity.this.getLayoutInflater();

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

    public void onBackPressed(){
        Intent gotoHome = new Intent(context, MainActivity.class);
        startActivity(gotoHome);
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
