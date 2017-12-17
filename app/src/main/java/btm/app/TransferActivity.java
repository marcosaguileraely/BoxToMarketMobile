package btm.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TransferActivity extends DataJp {
    private Button transferir;
    private EditText monto, idusert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.transfer_to_another_user));

        SharedPreferences pref = getSharedPreferences(getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = pref.getString(LoginActivity.USERNAME,"-");
        final String userp = pref.getString(LoginActivity.PASSPIN,"0000");

        transferir = (Button) findViewById(R.id.buttonTransfer);
        monto = (EditText) findViewById(R.id.editTextMontoTransfer);
        idusert = (EditText) findViewById(R.id.editTextIdTransfer);

        transferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monto.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.error_monto, Toast.LENGTH_LONG).show();
                    return;
                }
                if(idusert.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.id_del_destinatario, Toast.LENGTH_LONG).show();
                    return;
                }
                int val = Integer.valueOf(monto.getText().toString());
                if(val==0){
                    Toast.makeText(getApplicationContext(), R.string.error_monto, Toast.LENGTH_LONG).show();
                    return;
                }
                String userid = idusert.getText().toString();
                //transferencia
                //username, cc de envio, pin, monto
                jp_h(0x100,"162000", "", user, userid, userp, val, 12552);
            }
        });

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
