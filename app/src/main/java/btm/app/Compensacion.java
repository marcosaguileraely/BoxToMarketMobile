package btm.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

public class Compensacion extends DataJp {

    private Button clic;
    private EditText monto;
    private Spinner tipo;
    private TextView tvcuenta;
    private CuentaB cuenta;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compensacion);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.compensacion));

        SharedPreferences shPref = getSharedPreferences(getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = shPref.getString(LoginActivity.USERNAME,"--");
        final String userp = shPref.getString(LoginActivity.PASSPIN,"0000");

        clic         = (Button) findViewById(R.id.buttonCompensacion);
        monto        = (EditText) findViewById(R.id.editTextMonto);
        tipo         = (Spinner) findViewById(R.id.spinnerTipoCompensacion);
        tvcuenta     = (TextView) findViewById(R.id.textViewCuentaCompensacion);

        final Request request = new Request(this);
        String datos = "&username=" + user+"&token="+request.tk();

        request.http_get("datoscuenta", datos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if (response.isEmpty() || !response.contains("|")) {
                        tvcuenta.setText(R.string.alerta_cuentas);
                        clic.setEnabled(false);
                        return;
                    }
                    cuenta = new CuentaB(response);
                    tvcuenta.setText(cuenta.toString());
                } catch (Exception e) {
                    tvcuenta.setText(R.string.alerta_cuentas);
                }
            }
        });

        clic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monto.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.ingrese_valor, Toast.LENGTH_LONG).show();
                    return;
                }
                //datos:username|password|monto|tipo|numero|banco|
                String info_compensa = user+"|"+userp+"|"+monto.getText().toString()+"|"+tipo.getSelectedItem().toString()
                        +"|"+cuenta.getNumero()+"|"+cuenta.getBanco()+"|";
                String info = "&token="+request.tk()+"&datos="+ Base64.encodeToString(info_compensa.getBytes(), Base64.DEFAULT);
                //Log.d("DATOS_COMPENSACION", info);
                request.http_get("solicitarcompensacion", info.replace("\n",""), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        alert_info(response, getString(R.string.Info), android.R.drawable.ic_dialog_info);
                    }
                });
            }
        });
    }

    public void onBackPressed(){
        Intent gotoHome = new Intent(context, MainActivity.class);
        startActivity(gotoHome);
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
