package btm.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.ArrayList;

public class ChargeActivity extends AppCompatActivity {

    public static final String TAG = "DEV -> Add money";
    private Context context = this;

    private Button addMoney, addCreditCard;
    private EditText value, token;
    private Spinner addMoneyMethod, creditCardList;
    private ArrayList<CC> listTc;
    private TextView user_text;
    private String username_aux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        creditCardList    = (Spinner) findViewById(R.id.spinnerCreditCards);
        addMoneyMethod    = (Spinner) findViewById(R.id.spinnerTipoRecarga);
        token             = (EditText) findViewById(R.id.token);
        value             = (EditText) findViewById(R.id.value);
        addMoney          = (Button) findViewById(R.id.ChargeButton);
        addCreditCard     = (Button) findViewById(R.id.addCreditCardButton);

        listTc = new ArrayList<CC>();
        final DataJp dataJp = new DataJp();

        SharedPreferences shPref = getSharedPreferences(getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
        final String user = shPref.getString(LoginActivity.USERNAME,"--");
        final String userp = shPref.getString(LoginActivity.PASSPIN,"0000");
        username_aux = user;

        addMoneyMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String modo = parent.getItemAtPosition(position).toString();
                Log.d(TAG, modo);

                //if(modo.contains(getResources().getString(R.string.tipo_tarjeta))){
                if(modo.contains("Cr")){
                    Log.d(TAG, "1");

                    token.setVisibility(View.GONE);
                    value.setVisibility(View.VISIBLE);
                    addCreditCard.setVisibility(View.VISIBLE);
                    creditCardList.setVisibility(View.VISIBLE);

                    Log.d(TAG, "2");
                    Request request = new Request(getApplicationContext());
                    String datos = "&username="+user +"&token="+request.tk();

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
                            creditCardList.setAdapter(adapter);

                        }
                    });
                } else {
                    listTc = new ArrayList<CC>();
                    addCreditCard.setVisibility(View.GONE);
                    creditCardList.setVisibility(View.GONE);
                    value.setVisibility(View.GONE);
                    token.setVisibility(View.VISIBLE);
                    creditCardList.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChargeActivity.this, CardActivity.class);
                startActivity(intent);
            }
        });

        addMoney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG,"1");
                if(listTc.size() > 0){
                    Log.d(TAG,"2");
                    if(value.getText().toString().isEmpty()){
                        Toast.makeText(context, R.string.ingrese_valor, Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.d(TAG,"3");
                    int val = Integer.valueOf(value.getText().toString());
                    Log.d(TAG,"4");
                    dataJp.jp_h(0x100, "165000", user, userp, token.getText().toString(), "tarjeta|"+((CC)creditCardList.getSelectedItem()).getId(), val, 12552);
                    Log.d(TAG,"5");
                } else {
                    Log.d(TAG,"6");
                    if(token.getText().toString().isEmpty()){
                        Log.d(TAG,"7");
                        Toast.makeText(context, R.string.ingrese_token, Toast.LENGTH_LONG).show();
                        Log.d(TAG,"8");
                        return;
                    }
                    Log.d(TAG,"9");
                    dataJp.jp_h(0x100, "165000", user, userp, token.getText().toString(), "token", 1, 12552);
                    Log.d(TAG,"10");
                }
            }
        });
    }

    public void reloadCreditCardsList(){
            Request request = new Request(getApplicationContext());
            String datos    = "&username="+username_aux+"&token="+request.tk();

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
                    for (String cc : inf) {
                        listTc.add(new CC(cc));
                    }

                    ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                    // Specify the layout to use when the list of choices appears
                    creditCardList.setAdapter(adapter);
                }
            });
    }

    /*@Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.d(TAG, "I'm in back...");
        reloadCreditCardsList();
    }*/

}
