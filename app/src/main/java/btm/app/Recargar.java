package btm.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;

public class Recargar extends DataJp {

    private Button clic;
    private EditText monto, token;
    private Spinner metodo, tC;
    private ArrayList<CC> listTc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recargar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tC = (Spinner) findViewById(R.id.spinnerTC);
        metodo = (Spinner) findViewById(R.id.spinnerTipoRecarga);
        token = (EditText) findViewById(R.id.editTextToken);
        monto = (EditText) findViewById(R.id.editTextMonto);
        clic = (Button) findViewById(R.id.buttonRecargar);
        tC.setVisibility(View.GONE);

        listTc = new ArrayList<CC>();

        SharedPreferences shPref = getSharedPreferences(getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = shPref.getString(LoginActivity.USERNAME,"--");
        final String userp = shPref.getString(LoginActivity.PASSPIN,"0000");

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String modo = parent.getItemAtPosition(position).toString();
                if(modo.contains(getResources().getString(R.string.tipo_tarjeta))){
                    tC.setVisibility(View.VISIBLE);
                    monto.setVisibility(View.VISIBLE);
                    token.setVisibility(View.GONE);
                    Request request = new Request(getApplicationContext());
                    String datos = "&username="+user
                            +"&token="+request.tk();
                    request.http_get("menutarjetas", datos, new Response.Listener<String>() {
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
                            for (String cc :
                                    inf) {
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            // Specify the layout to use when the list of choices appears
                            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            tC.setAdapter(adapter);

                        }
                    });
                } else {
                    listTc = new ArrayList<CC>();
                    tC.setVisibility(View.GONE);
                    monto.setVisibility(View.GONE);
                    token.setVisibility(View.VISIBLE);
                    tC.setAdapter(null);
                }

                //Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


/*
        BtmRecargar   mti (165000)
        String monto = mensage.getString(4);      ////lo de siempre los doce digitos 000000000100
        String cedula = mensage.getString(60);
        String pin = mensage.getString(61);
        String token = mensage.getString(62);
        String _varios = mensage.getString(63); */     //_varios = metodopago|idtarjetas
        //metodos=token,tarjeta,paypal
        //jp_h(0x100, "165000", "Martin", "1982", "684976197", "token", 1, 12552);
        //jp_h(0x100, "165000", "Martin", "1982", "", "tarjeta|24", 1, 12552);

        clic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listTc.size() > 0){
                    if(monto.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.ingrese_valor, Toast.LENGTH_LONG).show();
                        return;
                    }
                    int val = Integer.valueOf(monto.getText().toString());
                    jp_h(0x100, "165000", user, userp, token.getText().toString(), "tarjeta|"+((CC)tC.getSelectedItem()).getId(),val,12552);
                } else {
                    if(token.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.ingrese_token, Toast.LENGTH_LONG).show();
                        return;
                    }
                    jp_h(0x100, "165000", user, userp, token.getText().toString(), "token",1,12552);
                }

            }
        });
    }

    @Override
    public void infoOk(String string, String pc) {
        super.infoOk(string, pc);
        //Log.d("DATAJPOS", string);
        //Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        alert_info(string, getString(R.string.confirmacion), android.R.drawable.ic_dialog_info);
    }

    @Override
    public void infoFalla(String string, String pc) {
        super.infoFalla(string, pc);
        //Log.d("DATAJPOS", string);
        alert_info(string, getString(R.string.Info), android.R.drawable.ic_dialog_alert);
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
