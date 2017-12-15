package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import btm.app.Network.NetActions;


public class ForgotPassActivity extends AppCompatActivity {

    Context context = this;

    private Button send_email;
    private EditText email;

    NetActions netActions = new NetActions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setContentView(R.layout.activity_forgot_pass);

        send_email = (Button) findViewById(R.id.forgot_btn1);
        email      = (EditText) findViewById(R.id.forgot_email);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //7getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_48dp);

        //Invoke the send email event
        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_email(v);
            }
        });
    }

    public void send_email(View view){
        final String email_txt = email.getText().toString();

        final String datos = email_txt;

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));

        Response.Listener<String> response = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response: ", response);
                progress.dismiss();

                if(response.toString().equals("Revisa tu email para más instrucciones")){
                    Log.d("Data response ok ", response.toString());
                    Toast.makeText(ForgotPassActivity.this, response, Toast.LENGTH_LONG).show();

                    email.setText(""); // Setting in blank the email field

                    Intent gotoLogin = new Intent(ForgotPassActivity.this, LoginActivity.class);
                    startActivity(gotoLogin);

                } else {
                    Log.d("Data response bad", response.toString());
                    Toast.makeText(ForgotPassActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        };
        netActions.resetPassword("olvidoclave", datos, response, progress);
    }

    public void onBackPressed(){
        Intent gotoLogin = new Intent(context, LoginActivity.class);
        startActivity(gotoLogin);
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
