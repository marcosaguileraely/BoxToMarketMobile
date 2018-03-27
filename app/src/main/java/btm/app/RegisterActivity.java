package btm.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import btm.app.Adapters.CountriesAdapter;
import btm.app.Adapters.SubscriptionAdapter;
import btm.app.Model.Countries;
import btm.app.Network.NetActions;

public class RegisterActivity extends DataJp {
    public static final String TAG = "DEV -> Register";
    private FragmentTransaction ft;
    private Spinner pais;
    private EditText username, pass_1, pass_2, email, nombres, apellidos, identificacion, celular;
    private CheckBox checkbox_terminos;
    Button saveData;
    ArrayList<Countries> listCountries;
    private CountriesAdapter adapter;


    //private String blockCharacterSet = "~#^|$%&*!ñÑáéíóú. ";
    String blockCharacterSet = "~#^|$%&*!";
    String data, countryName;
    int countryId;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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

        saveData = (Button) findViewById(R.id.button3);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchData();
            }
        });

        listCountries = new ArrayList<Countries>();
        renderCountryList();

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
                    username.setError("Ingrese un usuario");

                } else if (!nombre.matches("[a-zA-Z0-9_.? ]*")) {
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

        pais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String country = parent.getItemAtPosition(position).toString();
                countryName    = listCountries.get(position).getName();
                countryId      = listCountries.get(position).getId();
                setInitNumber(countryName);

                Log.d(TAG, "Country info: "+ countryId + " - " + countryName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void catchData(){
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
                   +"&pais="+countryId;
        try {
            data = new NetActions(context).register(url);
            Log.d(TAG, " oKHttp response: " + data);

            if(data.contains("Te hemos enviado un correo electrónico.")){
                alert_info_message(data, getString(R.string.Info), android.R.drawable.ic_dialog_info);
            }
            else{
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getAreaCode(String areacode){
        String[] separated = areacode.split("-");
        String finalAreaCode = separated[0].trim();
        Log.d(TAG, "areaCode: " + finalAreaCode);
        return finalAreaCode;
    }

    public void renderCountryList(){
        try {
            data = new NetActions(context).getCountries();
            Log.d(TAG, " oKHttp response: " + data);

            String[] data_split = data.split("\\|");
            for (String cc : data_split) {
                Log.d(TAG, " --> " + cc);

                String[] country_data_split = cc.split(",");
                for(int i=0; i<country_data_split.length - 1; i++){
                    String id   = country_data_split[0];
                    String name = country_data_split[1];
                    listCountries.add(new Countries(Integer.valueOf(id), name));
                }
            }
            adapter = new CountriesAdapter(context, R.layout.list_item_countries, listCountries);
            pais.setAdapter(adapter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alert_info_message(String res, String title, int icon){
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(res)
                .setIcon(icon)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });

        alert.show();
    }

    public void setInitNumber(String inDatum){
        if(inDatum.equals("Colombia")){
            celular.setText("+57");

        }if(inDatum.equals("Chile")){
            celular.setText("+56");

        }if(inDatum.equals("Mexico")){
            celular.setText("+52");

        }if(inDatum.equals("Estados Unidos")){
            celular.setText("+1");

        }if(inDatum.equals("España")){
            celular.setText("+34");

        }if(inDatum.equals("Alemania")){
            celular.setText("+49");

        }if(inDatum.equals("Argentina")){
            celular.setText("+54");

        }if(inDatum.equals("Australia")){
            celular.setText("+61");

        }if(inDatum.equals("Bolivia")){
            celular.setText("+591");

        }if(inDatum.equals("Brasil")){
            celular.setText("+55");

        }if(inDatum.equals("Canada")){
            celular.setText("+1");

        }if(inDatum.equals("Costa Rica")){
            celular.setText("+506");

        }if(inDatum.equals("China")){
            celular.setText("+86");

        }if(inDatum.equals("Francia")){
            celular.setText("+33");

        }if(inDatum.equals("Guatemala")){
            celular.setText("+502");

        }if(inDatum.equals("Honduras")){
            celular.setText("+504");

        }if(inDatum.equals("Hong Kong")){
            celular.setText("+852");

        }if(inDatum.equals("Holanda")){
            celular.setText("+31");

        }if(inDatum.equals("India")){
            celular.setText("+91");

        }if(inDatum.equals("Reino Unido")){
            celular.setText("+44");

        }if(inDatum.equals("Irlanda")){
            celular.setText("+353");

        }if(inDatum.equals("Italia")){
            celular.setText("+39");

        }if(inDatum.equals("Japon")){
            celular.setText("+81");

        }if(inDatum.equals("Nicaragua")){
            celular.setText("+505");

        }if(inDatum.equals("Panama")){
            celular.setText("+507");

        }if(inDatum.equals("Paraguay")){
            celular.setText("+595");

        }if(inDatum.equals("Peru")){
            celular.setText("+51");

        }if(inDatum.equals("Portugal")){
            celular.setText("+351");

        }if(inDatum.equals("Suiza")){
            celular.setText("+41");

        }if(inDatum.equals("Suecia")){
            celular.setText("+46");

        }if(inDatum.equals("Uruguay")){
            celular.setText("+598");

        }if(inDatum.equals("Venezuela")){
            celular.setText("+58");

        }if(inDatum.equals("Ecuador")){
            celular.setText("+598");

        }if(inDatum.equals("Republica Dominicana")){
            celular.setText("+1");

        }
    }


}