package btm.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

public class TransferCompensationActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Transfer ";
    Context context = this;
    AlertDialog dialog_pass_ui;

    Button transfer;
    EditText value_transfer;
    String datos;
    String password_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_compensation);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_transfer_comp_wallet));

        transfer       = (Button) findViewById(R.id.transfer_button);
        value_transfer = (EditText) findViewById(R.id.editTextMonto);

        datos  =  DataHolder.getData();

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Add the buttons
                builder.setMessage(R.string.ui_transfer_money_message_dialog);
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
        });
    }

    private class AsyncGetHttpData extends AsyncTask<String, Void, String> {
        ProgressDialog dialog2 = new ProgressDialog(TransferCompensationActivity.this);

        @Override
        protected void onPreExecute() {
            dialog2.setMessage(getString(R.string.inf_dialog));
            dialog2.show();
        }

        @Override
        protected String doInBackground(String... params) {
            executeHttpRequest(TransferCompensationActivity.this);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog2.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void passwordDialog(){
        AlertDialog.Builder builder_pass_dialog = new AlertDialog.Builder(context);
        final LayoutInflater inflater = TransferCompensationActivity.this.getLayoutInflater();

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

    public void executeHttpRequest(Activity view){
            view.runOnUiThread(new Runnable() {
                public void run(){
                    int value = Integer.parseInt(value_transfer.getText().toString());

                    Log.d(TAG, "" + datos);
                    String[] separated = datos.split("&");
                    String r1 = separated[0]; // this will contain "& blank"
                    String r2 = separated[1]; // this will contain "username"
                    String r3 = separated[2]; // this will contain "password"
                    Log.d(TAG, "r1: "+ r1 + " r2: " + r2 + " r3: " + r3);

                    String[] pinDataR2 = r2.split("=");
                    String data1   = pinDataR2[0];
                    String data2   = pinDataR2[1];
                    Log.d(TAG, "" + data1 + " - " + data2);

                    String[] pinDataR3 = r3.split("=");
                    String data3   = pinDataR3[0];
                    String data4   = pinDataR3[1];
                    Log.d(TAG, ""+ data3 + " - " + data4);

                    String fulldata = data2 + "|" + password_dialog + "|" + value; // Forma de enviar datos: username|pin|montousd => e.j: cachi|0000|2

                    Log.d(TAG, "" + fulldata);

                    try {
                        final String data = new btm.app.Network.NetActions(context).transferCompensation(fulldata);
                        Log.d(TAG, " -> " + data);

                        customDialog(data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
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
