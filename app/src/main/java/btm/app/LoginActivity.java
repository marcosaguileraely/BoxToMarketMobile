package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String PAIS = "PAIS";
    public static final String DATOS = "DATOS";
    public static final String PASSPIN = "PASSPIN";
    public static final String USERNAME = "USERNAME";
    public static final String TOKEN = "TOKEN";
    private EditText user, password;
    private Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        user     = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        login    = (Button) findViewById(R.id.buttonLogin);
        signup   = (Button) findViewById(R.id.signup);

        /*
         * Este ejecuta el método Ingrese cuando se presiona el botón.
         */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);  // Llamado al método: login
            }
        });

        /*
         * Este ejecuta el método Registrese cuando se presiona el botón.
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar(v);  // Llamado al método: registrar
            }
        });

    }

    public void registrar(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        this.startActivity(intent);

    }

    public void  forgetPass(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.boxtomarket.com/index.php?r=site%2Frequest-password-reset"));
        startActivity(intent);

    }

    public void login(View view){

        final String datos = "&username="+user.getText().toString()+
                             "&password="+password.getText().toString();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                // response
                //Log.d("Response", response);
                progress.dismiss();
                if(response.contains("VALIDO")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString(USERNAME, user.getText().toString())
                          .putString(PASSPIN, password.getText().toString())
                          .apply();
                    editor.commit();
                    Log.d("===>", editor.toString());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent.putExtra(PAIS, response.replace("|",";").split(";"))
                    .putExtra(DATOS, datos));

                    password.setText("");
                    user.setText("");

                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Request(this).http_get("login", datos, response, progress);
    }
}
