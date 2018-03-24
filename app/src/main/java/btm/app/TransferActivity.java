package btm.app;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import btm.app.DataHolder.DataHolder;

public class TransferActivity extends DataJp {
    public static final String TAG = "DEV -> Transfer money ";

    Context context = this;
    private Button transferir, transferComp;
    private EditText monto, idusert;
    private String username, password, userid, password_dialog;
    private int val;
    ProgressDialog progress;

    AlertDialog dialog_pass_ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.transfer_to_another_user));

        SharedPreferences pref = getSharedPreferences(getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = pref.getString(LoginActivity.USERNAME,"-");
        final String userp = pref.getString(LoginActivity.PASSPIN,"0000");

        transferir   = (Button) findViewById(R.id.buttonTransfer);
        transferComp = (Button) findViewById(R.id.transferCompButton);
        monto        = (EditText) findViewById(R.id.editTextMontoTransfer);
        idusert      = (EditText) findViewById(R.id.editTextIdTransfer);

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        progress.setCancelable(false);

        transferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username  = DataHolder.getUsername();
                password  = DataHolder.getPass();
                val       = convertMontoValue(monto.getText().toString());
                userid    = idusert.getText().toString();

                if(monto.getText().toString().isEmpty() || idusert.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.ui_transfer_money_message_fields_empty, Toast.LENGTH_LONG).show();
                    return;
                } else if(idusert.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.ui_transfer_money_message_zero_money, Toast.LENGTH_LONG).show();
                    return;
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    // Add the buttons
                    builder.setMessage(R.string.ui_transfer_money_message_dialog_to_another_user);
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

        transferComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoComp = new Intent(context, TransferCompensationActivity.class);
                startActivity(gotoComp);
            }
        });
    }

    public int convertMontoValue(String valur_str){
        if(valur_str.isEmpty()){
            return 0;
        }else{
            return Integer.valueOf(valur_str);
        }
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
                    String datos = "h0m3data|g0ldfish1|" + username + "|" + password_dialog + "|" + userid + "|" + val + "|";
                    Log.d(TAG, " --> " + password + " Datos : " + datos);

                    try {
                        final String data = new btm.app.Network.NetActions(context).transferMoneyToUser(datos);
                        Log.d(TAG, " oKHttp response: " + data);
                        progress.dismiss();
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
        final LayoutInflater inflater = TransferActivity.this.getLayoutInflater();

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
