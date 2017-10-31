package btm.app;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegisterActivity extends DataJp {
    private FragmentTransaction ft;
    private EditText username, pass_1, pass_2, email, nombres, apellidos, identificacion, celular;
    private Spinner pais;
    private String datos;
    private CheckBox checkbox_terminos;
    private Button saveData;

    private EditText editText;
    //private String blockCharacterSet = "~#^|$%&*!ñÑáéíóú. ";
    private String blockCharacterSet = "~#^|$%&*!";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username       = (EditText) findViewById(R.id.editText3);
        identificacion = (EditText) findViewById(R.id.editText9);

        saveData = (Button) findViewById(R.id.button3);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchData(v);
            }
        });

        //When username textEdit changes
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //username.setFilters(new InputFilter[] { filter });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String nombre = username.getText().toString();
                if (nombre.length() == 0) {

                    // Creamos el aviso
                    /*Toast aviso = Toast.makeText(getApplicationContext(),
                            "Por favor introduce un nombre de Usuario",
                            Toast.LENGTH_LONG);
                    aviso.show();*/
                    username.setError("Ingrese un usuario");

                } else if (!nombre.matches("[a-zA-Z0-9_.? ]*")) {
                    /*Toast aviso = Toast
                            .makeText(
                                    getApplicationContext(),
                                    "No son permitidos los espacios ni los caracteres especiales",
                                    Toast.LENGTH_LONG);
                    aviso.show();*/
                    username.setError("No son permitidos los espacios ni los caracteres especiales");

                } else {
                    // Do what ever you want
                }
            }
        });

        //When username textEdit is onFocus or not
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String nombre = username.getText().toString();

                if (hasFocus) {
                    //Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_SHORT).show();
                } else {
                    if (nombre.length() == 0) {
                        username.setError("Ingrese un usuario");
                    }
                }
            }
        });

        //When identificacion textEdit is onFocus or not
        identificacion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String id = identificacion.getText().toString();

                if (hasFocus) {
                    //Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_SHORT).show();
                } else {
                    if (id.length() == 0) {
                        identificacion.setError("Ingrese un No. identificación.");
                    }
                }
            }
        });


    }

    public void catchData(final View view){
        view.setEnabled(false);

        username          = (EditText) findViewById(R.id.editText3);
        pass_1            = (EditText) findViewById(R.id.editText4);
        pass_2            = (EditText) findViewById(R.id.editText5);
        email             = (EditText) findViewById(R.id.editText6);
        identificacion    = (EditText) findViewById(R.id.editText9);
        nombres           = (EditText) findViewById(R.id.editText7);
        apellidos         = (EditText) findViewById(R.id.editText8);
        pais              = (Spinner) findViewById(R.id.spinner);
        celular           = (EditText) findViewById(R.id.editText11);
        checkbox_terminos = (CheckBox) findViewById(R.id.checkBox);

        if(username.getText().toString().contains(" ")){
            Toast.makeText(this, R.string.alerta_usuario_espacio, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!pass_1.getText().toString().equals(pass_2.getText().toString())){
            Toast.makeText(this, R.string.alerta_usuario_pass, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!(pass_1.getText().toString().length() == 4)){
            Toast.makeText(this, R.string.alerta_usuario_pass_lon, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!email.getText().toString().contains("@") || !email.getText().toString().contains(".")){
            Toast.makeText(this, R.string.alerta_usuario_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkbox_terminos.isChecked()){
            Toast.makeText(this, R.string.alerta_terminos, Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
            return;
        }

        String[] apellidos_ = apellidos.getText().toString().split(" ");
        String apellido_2 = "";
        if(apellidos_.length > 1){
            for (int i = 1; i < apellidos_.length; i++) {
                apellido_2 = apellido_2 + apellidos_[i];
            }
        }
        String[] nombres_ = nombres.getText().toString().split(" ");
        String nombre_2 = "";
        if(nombres_.length > 1){
            for (int i = 1; i < nombres_.length; i++) {
                nombre_2 = nombre_2 + " " +nombres_[i];
            }
        }

        String username_ = username.getText().toString();
        try {
            nombres_[0]   = URLEncoder.encode(nombres_[0].trim(), "ISO-8859-1");
            apellidos_[0] = URLEncoder.encode(apellidos_[0].trim(), "ISO-8859-1");
            nombre_2      = URLEncoder.encode(nombre_2, "ISO-8859-1");
            apellido_2    = URLEncoder.encode(apellido_2, "ISO-8859-1");
            username_     = URLEncoder.encode(username_, "ISO-8859-1");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //"https://www.boxtomarket.com/index.php?r=app/registrar"
        String url ="&username="+ username_
                   +"&password="+pass_1.getText().toString()
                   +"&identificacion="+identificacion.getText().toString()
                   +"&nombre1=" + nombres_[0]                                   //+nombres.getText().toString()
                   +"&nombre2=" + ((nombres_.length > 1)?nombre_2:"-")          //+nombres.getText().toString()
                   +"&apellido1=" +apellidos_[0]                                //+apellidos.getText().toString()
                   +"&apellido2=" +((apellidos_.length > 1)?apellido_2:"-")     //+apellidos.getText().toString()
                   +"&email="+email.getText().toString()
                   +"&celular="+celular.getText().toString()
                   +"&pais="+pais.getSelectedItemPosition();

        new btm.app.Request(this).http_get("registrar", url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                if(response.contains("hemos enviado")){
                    alert_info(response, getString(R.string.Info), android.R.drawable.ic_dialog_info);
                } else
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    view.setEnabled(true);
            }
        });
    }

    //Not in use, but I leave it here for future implementations
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                Toast toast = Toast.makeText(context, "El usuario no permite caracteres especiales.", Toast.LENGTH_SHORT);
                //username.setError("Usuario no debe contener cacteres invalidos.");
                toast.show();
                return "";
            }
            return null;
        }
    };

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

    /*
    *
    * FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    *
    * */
}
