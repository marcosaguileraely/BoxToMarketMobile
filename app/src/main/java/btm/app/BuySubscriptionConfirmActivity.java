package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderSubs;
import btm.app.Network.NetActions;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BuySubscriptionConfirmActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Pagar subs";
    public Context context = this;

    public Button pay;
    public Spinner payment_method, credit_card;
    private ImageView detail_img;
    private TextView title, price, type;
    public EditText token;

    public ArrayList<CC> listTc;

    public String modo;
    public String card_info;
    public String card_id;
    public String token_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_subscription_confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NetActions netActions =  new NetActions(context);

        payment_method = (Spinner) findViewById(R.id.spinnerTipoRecarga);
        credit_card    = (Spinner) findViewById(R.id.spinnerCreditCards);
        token          = (EditText) findViewById(R.id.token);

        detail_img     = (ImageView) findViewById(R.id.pay_detail_img);
        title          = (TextView) findViewById(R.id.pay_detail_title);
        price          = (TextView) findViewById(R.id.pay_detail_price);
        type           = (TextView) findViewById(R.id.pay_detail_type);
        pay            = (Button) findViewById(R.id.pay_button);

        String img_uri    = DataHolderSubs.getImg_url();
        String type_prod  = "Tipo de producto: " + DataHolderSubs.getProduct_type();
        String price_prod = "Precio: " + DataHolderSubs.getPrice();

        token.setVisibility(GONE);
        listTc = new ArrayList<CC>();

        Glide.with(context)
                .load(img_uri)
                .centerCrop()
                .into(detail_img);
        type.setText(type_prod);
        price.setText(price_prod);
        title.setText(DataHolderSubs.getName());

        payment_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modo = parent.getItemAtPosition(position).toString();
                Log.d(TAG, modo);

                if( modo.equals("Tarjeta de Crédito") || modo.equals("Credit card")){
                    Log.d(TAG, "metodo tarjeta");
                    credit_card.setVisibility(VISIBLE);
                    token.setVisibility(GONE);

                    String datos = "&username="+ DataHolder.getUsername();
                    Log.d(TAG, "datos: "+datos);

                    netActions.http_get_data("menutarjetas", datos, new Response.Listener<String>() {
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

                            String[] inf = response.split("\\|");
                            for (String cc : inf) {
                                Log.d(TAG, "cc: "+cc);
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            credit_card.setAdapter(adapter);
                        }
                    });

                }
                if( modo.contains("Créditos BtM") || modo.contains("BtM Credit")){
                    Log.d(TAG, "metodo btm");
                    token.setVisibility(GONE);
                    credit_card.setVisibility(GONE);

                }
                if( modo.equals("token") || modo.equals("Token")){
                    Log.d(TAG, "metodo token");
                    token.setVisibility(VISIBLE);
                    credit_card.setVisibility(GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        credit_card.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                card_info = parent.getItemAtPosition(position).toString();
                card_id   = listTc.get(position).getId();
                Log.d(TAG, "card number: "+card_info + "-->"+ card_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Data = DataHolder.getData();
                String[] separated = Data.split("&");
                String r1 = separated[0]; // this will contain "& blank"
                String r2 = separated[1]; // this will contain "username"
                String r3 = separated[2]; // this will contain "password"
                Log.d(TAG, "r1: "+ r1 + " r2: " + r2 + " r3: " + r3);

                String[] pinData = r3.split("=");
                String passtxt = pinData[0];
                String pin     = pinData[1];
                Log.d(TAG, "passtxt: "+ passtxt + " pin: " + pin);

                token_number = token.getText().toString();

                //String card_id_aux = "";
                //String datos = "";

                /*if(modo.equals("Tarjeta de Crédito") || modo.equals("Credit card")){
                    card_id_aux = card_id;
                    token_number = "";

                    datos =   DataHolder.getUsername()
                            + "|" + pin
                            + "|" + convertPaymentMode(modo)
                            + "|" + card_id_aux
                            + "|" + DataHolderSubs.getId()
                            + "|";
                    Log.d(TAG, "datos -> "+ datos);


                }else if(modo.equals("token") || modo.equals("Token")){
                    token_number = token.getText().toString();
                    card_id_aux = "";

                    datos = "&datos=" + DataHolder.getUsername()
                            + "|" + pin
                            + "|" + convertPaymentMode(modo)
                            + "|" + token_number
                            + "|" + DataHolderSubs.getId()
                            + "|";
                    Log.d(TAG, "datos -> "+ datos);
                }
                else if(modo.contains("Créditos BtM") || modo.contains("BtM Credit")){
                    token_number = "";
                    card_id_aux = "";

                    datos = "&datos=" + DataHolder.getUsername()
                            + "|" + pin
                            + "|" + convertPaymentMode(modo)
                            + "|" + card_id_aux
                            + "|" + token_number
                            + "|" + DataHolderSubs.getId()
                            + "|";
                    Log.d(TAG, "datos -> "+ datos);
                }*/

                String datos = DataHolder.getUsername()
                             + "|" + pin
                             + "|" + convertPaymentMode(modo)
                             + "|" + card_id
                             + "|" + token_number
                             + "|" + DataHolderSubs.getId()
                             + "|";
                Log.d(TAG, "datos -> "+ datos);

                //if(token.getText().toString().length() > 0 ){ //Need to be sure if the user put a Token

                    final ProgressDialog progress = new ProgressDialog(context);
                    progress.setMessage(getString(R.string.inf_dialog));
                    //progress.show();

                    Response.Listener<String> response = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "1 "+ response);

                            progress.dismiss();

                            if(response.contains("exitosa")){
                                SharedPreferences sharedPref    = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();

                                Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    new btm.app.Network.NetActions(context).buySubscription(datos, response, progress);

                //}else{
                //    Toast.makeText(context, "Ingrese un token valido", Toast.LENGTH_LONG).show();
                //}
            }
        });
    }

    public String convertPaymentMode(String mode){
        String finalmodel = "";

        if(mode.equals("Tarjeta de Crédito") || mode.equals("Credit card")){
            return finalmodel = "Tarjeta";

        }
        if(mode.contains("Créditos BtM") || mode.contains("BtM Credit")){
            return finalmodel = "Creditos";

        }
        if(mode.equals("token") || mode.equals("Token")){
            return finalmodel = "Token";
        }

        return finalmodel;
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
