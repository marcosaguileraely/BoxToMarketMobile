package btm.app;

import android.annotation.SuppressLint;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends DataJp {

    private FragmentTransaction ft;
    private EditText username, pass_1, pass_2, email, nombres, apellidos, identificacion, celular;
    private Spinner pais;
    private String datos;
    private CheckBox checkbox_terminos;

    @SuppressLint("ValidFragment")
    public static class FooFragment extends Fragment {
        // The onCreateView method is called when Fragment should create its View object hierarchy,
        // either dynamically or via XML layout inflation.
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.register_one, parent, false);
        }

        // This event is triggered soon after onCreateView().
        // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            // Setup any handles to view objects here
            // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        }
    }

    @SuppressLint("ValidFragment")
    public static class FooFragmentTwo extends Fragment {

        // The onCreateView method is called when Fragment should create its View object hierarchy,
        // either dynamically or via XML layout inflation.

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.register_two, parent, false);
        }

        // This event is triggered soon after onCreateView().
        // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            // Setup any handles to view objects here
            // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

            //String [] paises = getResources().getStringArray(R.array.pais_select);
            /*Spinner s8 = (Spinner) view.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.pais_select, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s8.setAdapter(adapter);*/

        }
    }


    public void prueba(View view){
        //username
        username = (EditText) findViewById(R.id.editText3);
        //password
        pass_1 = (EditText) findViewById(R.id.editText4);
        pass_2 = (EditText) findViewById(R.id.editText5);
        //email
        email = (EditText) findViewById(R.id.editText6);
        //identificacion
        identificacion = (EditText) findViewById(R.id.editText9);

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

        ft = getSupportFragmentManager().beginTransaction();
        //ft.remove(new FooFragment()).commit();

        ft.replace(R.id.reg_placeholder, new FooFragmentTwo());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.addToBackStack(null);

        ft.commit();
    }

    public void catchData(final View view){

        view.setEnabled(false);

        //nombres
        nombres = (EditText) findViewById(R.id.editText7);
        //apellidos
        apellidos = (EditText) findViewById(R.id.editText8);
        //pais
        pais = (Spinner) findViewById(R.id.spinner);
        //celular
        celular = (EditText) findViewById(R.id.editText11);

        checkbox_terminos = (CheckBox) findViewById(R.id.checkBox);

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
            nombres_[0] = URLEncoder.encode(nombres_[0].trim(), "ISO-8859-1");
            apellidos_[0] = URLEncoder.encode(apellidos_[0].trim(), "ISO-8859-1");
            nombre_2 = URLEncoder.encode(nombre_2, "ISO-8859-1");
            apellido_2 = URLEncoder.encode(apellido_2, "ISO-8859-1");
            username_ = URLEncoder.encode(username_, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //"https://www.boxtomarket.com/index.php?r=app/registrar"
        String url ="&username="+ username_
                +"&password="+pass_1.getText().toString()
                +"&identificacion="+identificacion.getText().toString()
                +"&nombre1=" + nombres_[0]//+nombres.getText().toString()
                +"&nombre2=" + ((nombres_.length > 1)?nombre_2:"-")//+nombres.getText().toString()
                +"&apellido1=" +apellidos_[0]//+apellidos.getText().toString()
                +"&apellido2=" +((apellidos_.length > 1)?apellido_2:"-")//+apellidos.getText().toString()
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Begin the transaction
        ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.add(R.id.reg_placeholder, new FooFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

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
