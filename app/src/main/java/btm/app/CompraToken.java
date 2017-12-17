package btm.app;

import android.content.Context;
import android.content.SharedPreferences;
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

public class CompraToken extends DataJp{
    private Spinner tC, metodo;
    private EditText email, monto;
    private Button clic;
    private ArrayList<CC> listTc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_token);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.buy_a_token_title));

        SharedPreferences pref = getSharedPreferences(getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = pref.getString(LoginActivity.USERNAME,"-");
        final String userp = pref.getString(LoginActivity.PASSPIN,"0000");

        tC = (Spinner) findViewById(R.id.spinnerTipoTC);
        metodo = (Spinner) findViewById(R.id.spinnerTipoCompra);
        email = (EditText) findViewById(R.id.editTextEmail);
        monto = (EditText) findViewById(R.id.editTextMonto);
        clic = (Button) findViewById(R.id.buttonCompraToken);
        tC.setVisibility(View.GONE);

        listTc = new ArrayList<CC>();

        metodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String modo = parent.getItemAtPosition(position).toString();
                if(modo.contains(getResources().getString(R.string.tipo_tarjeta))){
                    tC.setVisibility(View.VISIBLE);
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
                                tC.setVisibility(View.GONE);
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
                    tC.setAdapter(null);
                }

                //Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monto.getText().toString().isEmpty() || email.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.campos_vacios, Toast.LENGTH_LONG).show();
                    return;
                }
                int val = Integer.valueOf(monto.getText().toString());
                if(val==0){
                    Toast.makeText(getApplicationContext(), R.string.error_monto, Toast.LENGTH_LONG).show();
                    return;
                }
                if(listTc.size() > 0){
                    jp_h(0x100, "164000", user, userp, email.getText().toString(), "tarjeta|"+((CC)tC.getSelectedItem()).getId()+"|",val,12552);
                } else {
                    jp_h(0x100, "164000", user, userp, email.getText().toString(), "creditos||",val,12552);
                }

            }
        });
        //_varios = metodopago|idtarjetas|asociado_fecel
        //metodo=creditos,tarjeta,paypal
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
}
