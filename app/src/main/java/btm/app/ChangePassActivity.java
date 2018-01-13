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

public class ChangePassActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Change pass ";

    private Button change;
    private EditText actualPass, newPass, reNewPass;
    String data_actualPass, data_newPass, data_reNewaPass, username;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.change_password));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        change      = (Button) findViewById(R.id.buttonChangePass);
        actualPass  = (EditText) findViewById(R.id.actual_pass);
        newPass     = (EditText) findViewById(R.id.newpass);
        reNewPass   = (EditText) findViewById(R.id.renewpass);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data_actualPass = actualPass.getText().toString();
                data_newPass    = newPass.getText().toString();
                data_reNewaPass = reNewPass.getText().toString();
                username        = DataHolder.getUsername();

                if (data_actualPass.isEmpty() || data_newPass.isEmpty() || data_reNewaPass.isEmpty()){
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                }

                else if(!data_newPass.equals(data_reNewaPass)){
                    Toast.makeText(context, "Password confirmation doesn't match", Toast.LENGTH_SHORT).show();
                }

                else{
                    new AsyncGetHttpData().execute("");
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
            executeHttpRequest(ChangePassActivity.this);
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

                String fulldata = username + "|" + data_actualPass + "|" + data_newPass + "|";
                // Forma de enviar datos: username|clave|nuevaclave => e.j: cachi|xxxx|xxyy
                Log.d(TAG, " Datos : " + fulldata);

                try {
                    final String data = new btm.app.Network.NetActions(context).changePassword(fulldata);
                    Log.d(TAG, " oKHttp response: " + data);

                    if(data.equals("El cambio de clave fue efectuado con exito")){

                        actualPass.setText("");
                        newPass.setText("");
                        reNewPass.setText("");

                        Toast.makeText(context, getString(R.string.ui_change_pass_msg_sucess), Toast.LENGTH_LONG).show();
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
