package btm.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import btm.app.DataHolder.DataHolder;

public class TransferCompensationActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Transfer ";
    Context context = this;

    Button transfer;
    EditText value_transfer;
    String datos;

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
                new AsyncGetHttpData().execute("");
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
            executeHttpRequest(TransferCompensationActivity.this);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
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

                    String fulldata = data2 + "|" + data4 + "|" + value; // Forma de enviar datos: username|pin|montousd => e.j: cachi|0000|2

                    Log.d(TAG, "" + fulldata);

                    try {
                        final String data = new btm.app.Network.NetActions(context).transferCompensation(fulldata);
                        Log.d(TAG, "xxx xxx xxx " + data);

                        if(data.equals("La transferencia ha sido exitosa.")){
                            Toast.makeText(context, data, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(context, data, Toast.LENGTH_LONG).show();
                        }

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
}
